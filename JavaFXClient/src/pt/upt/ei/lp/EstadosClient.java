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
import javafx.collections.ListChangeListener.Change;
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

import pt.upt.ei.lp.db.Estado;
import pt.upt.ei.lp.db.Recorrencia;

public class EstadosClient extends Application{
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/RESTServer/estado/").build();
	}
	
	private static ClientConfig config = new ClientConfig();
	private static Client client = ClientBuilder.newClient(config);
	private static WebTarget service = client.target(getBaseURI());
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception{
		
		GridPane root = new GridPane();
		
		TableView tableView = new TableView();
		
		TableColumn<Estado, String> column1 = new TableColumn<>("Estado Estado");
		column1.setMinWidth(200);
		column1.setCellValueFactory(new PropertyValueFactory<>("estado"));
		
		TableColumn<Estado, String> column2 = new TableColumn<>("Tarefas");
		column1.setMinWidth(200);
		column2.setCellValueFactory(new PropertyValueFactory<>("tarefas"));
		
		TableColumn<Estado, Boolean> column3 = new TableColumn<>("Is Available?");
        column1.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("available"));
        
        tableView.getColumns().add(column1);
		tableView.getColumns().add(column2);
		tableView.getColumns().add(column3);

		fillTableView(tableView);
		
		TableView.TableViewSelectionModel<Estado> selectionModel = tableView.getSelectionModel();
		
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
        
        ObservableList<Estado> selectedItems = selectionModel.getSelectedItems();
        
        selectedItems.addListener(new ListChangeListener<Estado>() {
            @Override
            public void onChanged(Change<? extends Estado> change) {
                System.out.println("Selection changed: " + change.getList());
            }
        });
        
        selectionModel.select(0);
        
        ObservableList<Integer> selectedIndices = selectionModel.getSelectedIndices();
        
        tableView.prefHeightProperty().bind(primaryStage.heightProperty());
        tableView.prefWidthProperty().bind(primaryStage.widthProperty());
        
        root.add(tableView, 0, 0, 4, 1);
		
        Button btnNew = new Button("New");
		Button btnEdit = new Button("Edit");	
		Button btnDelete = new Button("Delete");
		Button btnCancel = new Button("Cancel");
		
		btnNew.setOnAction(ae -> { 
			System.out.println("New estado data... ");
			showAddUpdateEstadoStage(primaryStage, new Estado()); 
			fillTableView(tableView);
        }); 
		
		btnEdit.setOnAction(ae -> {
			System.out.println("Editing data... ");			
			showAddUpdateEstadoStage(primaryStage, selectedItems.get(0));
			fillTableView(tableView);
		});
		
		btnCancel.setOnAction(ae -> {
			System.out.println("Cancelling...");
			fillTableView(tableView);
		});
		
		root.add(btnNew, 0, 1, 1, 1);
		root.add(btnEdit, 1, 1, 1, 1);
		root.add(btnDelete, 2, 1, 1, 1);
		root.add(btnCancel, 3, 1, 1, 1);
		
		root.setHgap(5);
		root.setVgap(5);
						
		Scene scene = new Scene(root);
		primaryStage.setTitle("Simple Estados CRUD example");
		primaryStage.setScene(scene);
		
		primaryStage.setX(300);
		primaryStage.setY(300);
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		
		primaryStage.show(); 
	}
	
	private void fillTableView(TableView tableView) {
        tableView.getItems().clear();
        List<Estado> estados = getEstados();
        for (Estado e: estados) {
        	tableView.getItems().add(e);
		}
	}
	
	private List<Estado> getEstados() {

		// Get the Books
		Gson gson = new Gson();

		String responseTarefasList = service.path("getEstados")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(String.class);

		List<Estado> estados = Arrays.asList(gson.fromJson(responseTarefasList, Estado[].class));
				
		return estados;
	}
	
	private void showAddUpdateEstadoStage(Stage primaryStage, Estado estado) {
		
		Stage stage = new Stage();
		
		stage.setTitle("Adicionar ou atualizar Estados - in Modal Mode");
        stage.setX(300);
        stage.setY(300);
        stage.setWidth(300);
        stage.setHeight(300);
        
        GridPane root = new GridPane();
        
        Label lblEstado = new Label("Estado");
        root.add(lblEstado, 0, 0, 1, 1);
        
        TextField txtEstado = new TextField();
        lblEstado.setId("lblEstado");
        root.add(lblEstado, 1, 0, 1 ,1);
        
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
		
		if (estado.getEstado() != null) {
			txtEstado.setText(estado.getEstado());	
		}
		
		btnSave.setOnAction(ae -> {
			System.out.println("A guardar... " + estado);
			
			estado.setEstado(txtEstado.getText());
			
			saveData(estado);
			stage.close();
		});
		
		btnCancel.setOnAction(ae -> {
			System.out.println("Cancelling...ANdre é gay");
			cleanFields(root);
			stage.close();
		});
		
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
	
	private void saveData(Estado estado) {
		try {
			
			Response response;
			String message = "Estado adicionada com sucesso";
			
			if(estado.getEstado() != null) {
				message = "Estado adicionado com sucesso";
				response = service.path("updateEstado").request(MediaType.APPLICATION_JSON)
						.put(Entity.entity(estado, MediaType.APPLICATION_JSON), Response.class);
			}else {
				message = "Estado adicionado com sucesso";
				response = service.path("addEstado").request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(estado, MediaType.APPLICATION_JSON), Response.class);
			}
			
			if (response.getStatus() < 200 || response.getStatus() > 299) {			
				showMessage("Failed : HTTP error code : " + response.getStatus(), AlertType.ERROR);
				//throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}else {
				showMessage(message, AlertType.INFORMATION);
			}
		} catch (Exception e) {
			showMessage("Erro ao guardar o estado", AlertType.ERROR);
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
        alert.setTitle("Estados Application");
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

		alert.setTitle("Delete estado warning");
		Optional<ButtonType> result = alert.showAndWait();

		return (result.orElse(closeBtn) == okBtn);
	}
}
