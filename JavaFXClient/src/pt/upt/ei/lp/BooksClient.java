package pt.upt.ei.lp;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.gson.Gson;

import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pt.upt.ei.lp.db.Book;

public class BooksClient extends Application {
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/RESTServer/book/").build();
	}

	private static ClientConfig config = new ClientConfig();
	private static Client client = ClientBuilder.newClient(config);
	private static WebTarget service = client.target(getBaseURI());
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		//FlowPane root = new FlowPane();
		GridPane root = new GridPane();
		
		/* We will use the TableView component to list the books */
		
		TableView tableView = new TableView();

		/* Each of the columns must be linked to a colum in your model class (Book) - 
		 * Pay attention to the type
		 */		
        TableColumn<Book, String> column1 = new TableColumn<>("Author Name");
        column1.setMinWidth(200);
        column1.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> column2 = new TableColumn<>("Title");
        column1.setMinWidth(200);
        column2.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Book, Boolean> column3 = new TableColumn<>("Is Available?");
        column1.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("available"));

        /* Add the columns to the table view */
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        
        // Load objects into table calling the REST service
        fillTableView(tableView);

        /*This command line gets the selected row and the corresponding Model Instance (Book)*/
        TableView.TableViewSelectionModel<Book> selectionModel = tableView.getSelectionModel();

        /* You can choose between single and multiple selection*/
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        //selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        /* Here is a list for the selected items in the table view */
        ObservableList<Book> selectedItems = selectionModel.getSelectedItems();

        /* In case you need to check the selected item when it is changed */
        selectedItems.addListener(new ListChangeListener<Book>() {
            @Override
            public void onChanged(Change<? extends Book> change) {
                System.out.println("Selection changed: " + change.getList());
            }
        });

        /* Select the first item of the table view */
        selectionModel.select(0);

        /* In case if you need to get the index of the table view */
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        
        //tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        //root.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        
        // We bind the prefHeight- and prefWidthProperty to the height and width of the stage.
        tableView.prefHeightProperty().bind(primaryStage.heightProperty());
        tableView.prefWidthProperty().bind(primaryStage.widthProperty());
        
        /* 
         * or we can use the scroll pane
         * 
        ScrollPane sp = new ScrollPane(tableView);
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        
        */
        
        //root.getChildren().add(tableView);  
        root.add(tableView, 0, 0, 4, 1);
		
        Button btnNew = new Button("New");
		Button btnEdit = new Button("Edit");	
		Button btnDelete = new Button("Delete");
		Button btnCancel = new Button("Cancel");
		
		btnNew.setOnAction(ae -> { 
			System.out.println("New book data... ");
			showAddUpdateBookStage(primaryStage, new Book()); 
			fillTableView(tableView);
        });
						
		btnEdit.setOnAction(ae -> {
			System.out.println("Editing data... ");			
			showAddUpdateBookStage(primaryStage, selectedItems.get(0));
			fillTableView(tableView);
		});
		
		btnDelete.setOnAction(ae -> {
			System.out.println("Deleting data... ");				

			if (showConfirmationDialog("Are you sure you want to delete the book? ")) {
				deleteBook(selectedItems.get(0).getId());
				fillTableView(tableView);
			}			
			
		});
		
		btnCancel.setOnAction(ae -> {
			System.out.println("Cancelling...");
			fillTableView(tableView);
		});
		
				
		//root.getChildren().addAll(new Button[] { btnNew, btnEdit, btnDelete, btnCancel});
		root.add(btnNew, 0, 1, 1, 1);
		root.add(btnEdit, 1, 1, 1, 1);
		root.add(btnDelete, 2, 1, 1, 1);
		root.add(btnCancel, 3, 1, 1, 1);
		
		root.setHgap(5);
		root.setVgap(5);
						
		Scene scene = new Scene(root);
		primaryStage.setTitle("Simple Books CRUD example");
		primaryStage.setScene(scene);
		
		primaryStage.setX(300);
		primaryStage.setY(300);
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		
		primaryStage.show();
	}
	
	private void fillTableView(TableView tableView) {
        tableView.getItems().clear();
        List<Book> books = getBooks();
        for (Book b : books) {
        	tableView.getItems().add(b);
		}
	}

	private List<Book> getBooks() {

		// Get the Books
		Gson gson = new Gson();

		String responseBooksList = service.path("getBooks")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(String.class);

		List<Book> books = Arrays.asList(gson.fromJson(responseBooksList, Book[].class));
				
		return books;

	}
	
	private boolean deleteBook(int bookId) {

		// Delete Book with id 1
		Response response = service.path("deleteBook")
				.path(Integer.toString(bookId)).request().delete();

		if (response.getStatus() < 200 || response.getStatus() > 299) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} 
		
		return response.getStatus() == 200;

	}
	
    private void showAddUpdateBookStage(Stage primaryStage, Book book) {
    	    	   	
    	Stage stage = new Stage();
        
    	stage.setTitle("Add or Update Books - in Modal Mode");
        stage.setX(300);
        stage.setY(300);
        stage.setWidth(300);
        stage.setHeight(300);
        
		//FlowPane root = new FlowPane();
		GridPane root = new GridPane();

		Label lblTitle = new Label("Title");
		root.add(lblTitle, 0, 0, 1, 1);
		//root.getChildren().add(lblTitle);

		TextField txtTitle = new TextField();
		lblTitle.setId("lblTitle");
		root.add(txtTitle, 1, 0, 1, 1);
		//root.getChildren().add(txtTitle);

		Label lblAuthor = new Label("Author");
		//root.getChildren().add(lblAuthor);
		root.add(lblAuthor, 0, 1, 1, 1);

		TextField txtAuthor = new TextField();
		txtAuthor.setId("txtAuthor");
		root.add(txtAuthor, 1, 1, 1, 1);
		//root.getChildren().add(txtAuthor);

		Label lblIsAvailable = new Label("Is Available");
		root.add(lblIsAvailable, 0, 2, 1, 1);
		//root.getChildren().add(lblIsAvailable);

		CheckBox ckIsAvailable = new CheckBox();
		ckIsAvailable.setId("ckIsAvailable");
		root.add(ckIsAvailable, 1, 2, 1, 1);
		//root.getChildren().add(ckIsAvailable);

		Button btnSave = new Button("Save");
		Button btnCancel = new Button("Cancel");
		root.add(btnSave, 0, 3, 1, 1);
		root.add(btnCancel, 1, 3, 1, 1);
		
		root.setHgap(5);
		root.setVgap(5);
		
		if (book.getId() != 0) {
			txtTitle.setText(book.getTitle());
			txtAuthor.setText(book.getAuthor());
			ckIsAvailable.setSelected(book.getAvailable());
		}

		btnSave.setOnAction(ae -> {
			System.out.println("Saving data... " + book);
			
			book.setTitle(txtTitle.getText());
			book.setAuthor(txtAuthor.getText());
			book.setAvailable(ckIsAvailable.isSelected());
			
			saveData(book);
			stage.close();
		});

		btnCancel.setOnAction(ae -> {
			System.out.println("Cancelling...");
			cleanFields(root);
			stage.close();
		});

		//root.getChildren().addAll(new Button[] { btnSave, btnCancel });
		
		root.setAlignment(Pos.CENTER);

		Scene scene = new Scene(root);
		stage.setTitle("Simple Input Screen Example");
		stage.setScene(scene);
		      
        
        stage.initOwner(primaryStage);
        //stage.initModality(Modality.NONE);
        stage.initModality(Modality.WINDOW_MODAL);
        //stage.initModality(Modality.APPLICATION_MODAL);


        stage.showAndWait();
    }
    
    private void saveData(Book book) {		
		try {
			
			Response response;
			String message = "Book added successfully.";
			
			if (book.getId() != 0) {
				
				message = "Book updated successfully.";				
				response = service.path("updateBook").request(MediaType.APPLICATION_JSON)
						.put(Entity.entity(book, MediaType.APPLICATION_JSON), Response.class);
			} else {
				
				message = "Book added successfully.";
				response = service.path("addBook").request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(book, MediaType.APPLICATION_JSON), Response.class);
			}		
			
			if (response.getStatus() < 200 || response.getStatus() > 299) {			
				showMessage("Failed : HTTP error code : " + response.getStatus(), AlertType.ERROR);
				//throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}else {
				showMessage(message, AlertType.INFORMATION);
			}
		} catch (Exception e) {
			showMessage("Error while saving the book.", AlertType.ERROR);
			//throw new RuntimeException("Failed to save the book.");
		}
	}
	
	private void cleanFields(GridPane root) {
		
		for (Node node : root.getChildren()) {
		    System.out.println("Id: " + node.getId());
		    if (node instanceof TextField) {
		        // clear
		        ((TextField)node).setText("");
		    } else if (node instanceof CheckBox) {
		        // clear
		        ((CheckBox)node).setSelected(false);
		    }
		}
		
	}
	
	private void showMessage(String message, AlertType alertType) {
		Alert alert = new Alert(alertType);
        alert.setTitle("Books Application");
        alert.setHeaderText(message);
        //alert.setContentText(message);
        alert.showAndWait();
	}
	
	private boolean showConfirmationDialog(String confirmationMessage) {
		ButtonType okBtn = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType closeBtn = new ButtonType("Close", ButtonData.CANCEL_CLOSE);
		Alert alert = new Alert(AlertType.WARNING,
		        confirmationMessage,
		        okBtn,
		        closeBtn);

		alert.setTitle("Delete book warning");
		Optional<ButtonType> result = alert.showAndWait();

		return (result.orElse(closeBtn) == okBtn);
	}
	
}