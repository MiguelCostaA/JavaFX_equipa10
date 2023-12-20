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

import pt.upt.ei.lp.db.Recorrencia;
import pt.upt.ei.lp.db.Utilizador;

public class RecorrenciasClient extends Application {
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/RESTServer/recorrencia/").build();
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
		
		TableColumn<Recorrencia, String> column1 = new TableColumn<>("Recorrencia Recorrencia");
		column1.setMinWidth(200);
		column1.setCellValueFactory(new PropertyValueFactory<>("recorrencia"));
		
		TableColumn<Recorrencia, String> column2 = new TableColumn<>("Tarefas");
		column1.setMinWidth(200);
		column2.setCellValueFactory(new PropertyValueFactory<>("tarefas"));
		
		TableColumn<Recorrencia, Boolean> column3 = new TableColumn<>("Is Available?");
        column1.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("available"));
		
		tableView.getColumns().add(column1);
		tableView.getColumns().add(column2);
		tableView.getColumns().add(column3);

		
		fillTableView(tableView);
		
		TableView.TableViewSelectionModel<Recorrencia> selectionModel = tableView.getSelectionModel();
		
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
        
        ObservableList<Recorrencia> selectedItems = selectionModel.getSelectedItems();
        
        selectedItems.addListener(new ListChangeListener<Recorrencia>() {
            @Override
            public void onChanged(Change<? extends Recorrencia> change) {
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
			System.out.println("New recorrencia data... ");
			showAddUpdateRecorrenciaStage(primaryStage, new Recorrencia()); 
			fillTableView(tableView);
        });
		
		btnEdit.setOnAction(ae -> {
			System.out.println("Editing data... ");			
			showAddUpdateRecorrenciaStage(primaryStage, selectedItems.get(0));
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
		primaryStage.setTitle("Simple Recorrencias CRUD example");
		primaryStage.setScene(scene);
		
		primaryStage.setX(300);
		primaryStage.setY(300);
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		
		primaryStage.show();
	}
	
	private void fillTableView(TableView tableView) {
        tableView.getItems().clear();
        List<Recorrencia> recorrencias = getRecorrencias();
        for (Recorrencia r : recorrencias) {
        	tableView.getItems().add(r);
		}
	}
	
	private List<Recorrencia> getRecorrencias() {

		// Get the Books
		Gson gson = new Gson();

		String responseTarefasList = service.path("getRecorrencias")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(String.class);

		List<Recorrencia> recorrencias = Arrays.asList(gson.fromJson(responseTarefasList, Recorrencia[].class));
				
		return recorrencias;
	}
	
	private void showAddUpdateRecorrenciaStage(Stage primaryStage, Recorrencia recorrencia) {
		
		Stage stage = new Stage();
		
		stage.setTitle("Adicionar ou atualizar Recorrencias - in Modal Mode");
        stage.setX(300);
        stage.setY(300);
        stage.setWidth(300);
        stage.setHeight(300);
        
        GridPane root = new GridPane();
        
        Label lblRecorrencia = new Label("Recorrencia");
        root.add(lblRecorrencia, 0, 0, 1, 1);
        
        TextField txtRecorrencia = new TextField();
        lblRecorrencia.setId("lblRecorrencia");
        root.add(lblRecorrencia, 1, 0, 1 ,1);
        
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
		
		if (recorrencia.getRecorrencia() != null) {
			txtRecorrencia.setText(recorrencia.getRecorrencia());	
		}
		
		btnSave.setOnAction(ae -> {
			System.out.println("A guardar... " + recorrencia);
			
			recorrencia.setRecorrencia(txtRecorrencia.getText());
			
			saveData(recorrencia);
			stage.close();
		});
		
		btnCancel.setOnAction(ae -> {
			System.out.println("Cancelling...");
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
	
	private void saveData(Recorrencia recorrencia) {
		try {
			
			Response response;
			String message = "Recorrência adicionada com sucesso";
			
			if(recorrencia.getRecorrencia() != null) {
				message = "Recorrência adicionada com sucesso";
				response = service.path("updateRecorrencia").request(MediaType.APPLICATION_JSON)
						.put(Entity.entity(recorrencia, MediaType.APPLICATION_JSON), Response.class);
			}else {
				message = "Recorrência adicionada com sucesso";
				response = service.path("addRecorrencia").request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(recorrencia, MediaType.APPLICATION_JSON), Response.class);
			}
			
			if (response.getStatus() < 200 || response.getStatus() > 299) {			
				showMessage("Failed : HTTP error code : " + response.getStatus(), AlertType.ERROR);
				//throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}else {
				showMessage(message, AlertType.INFORMATION);
			}
		} catch (Exception e) {
			showMessage("Erro ao guardar a recorrência", AlertType.ERROR);
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
        alert.setTitle("Recorrencias Application");
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

		alert.setTitle("Delete recorrência warning");
		Optional<ButtonType> result = alert.showAndWait();

		return (result.orElse(closeBtn) == okBtn);
	}
}
