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

import pt.upt.ei.lp.db.Tarefa;

public class TarefasClient extends Application {
	
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/RESTServer/tarefa/").build();
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
		
		TableColumn<Tarefa, String> column1 = new TableColumn<>("Tarefa Titulo");
		column1.setMinWidth(200);
		column1.setCellValueFactory(new PropertyValueFactory<>("titulo"));
		
		TableColumn<Tarefa, String> column2 = new TableColumn<>("Tarefa Descricao");
		column1.setMinWidth(200);
		column2.setCellValueFactory(new PropertyValueFactory<>("descricao"));
		
		TableColumn<Tarefa, String> column3 = new TableColumn<>("Tarefa DataConclusao");
		column1.setMinWidth(200);
		column3.setCellValueFactory(new PropertyValueFactory<>("dataConclusao"));
		
		TableColumn<Tarefa, String> column4 = new TableColumn<>("Tarefa Prioridade");
		column1.setMinWidth(200);
		column4.setCellValueFactory(new PropertyValueFactory<>("prioridade"));
		
				
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);
     
        
        fillTableView(tableView);
        
        TableView.TableViewSelectionModel<Tarefa> selectionModel = tableView.getSelectionModel();
        
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        
        ObservableList<Tarefa> selectedItems = selectionModel.getSelectedItems();
        
        selectedItems.addListener(new ListChangeListener<Tarefa>() {
            @Override
            public void onChanged(Change<? extends Tarefa> change) {
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
			System.out.println("New tarefa data... ");
			showAddUpdateTarefaStage(primaryStage, new Tarefa()); 
			fillTableView(tableView);
        });
		
		btnEdit.setOnAction(ae -> {
			System.out.println("Editing data... ");			
			showAddUpdateTarefaStage(primaryStage, selectedItems.get(0));
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
		primaryStage.setTitle("Simple Tarefas CRUD example");
		primaryStage.setScene(scene);
		
		primaryStage.setX(300);
		primaryStage.setY(300);
		primaryStage.setWidth(500);
		primaryStage.setHeight(500);
		
		primaryStage.show();
	}
	
	private void fillTableView(TableView tableView) {
        tableView.getItems().clear();
        List<Tarefa> tarefas = getTarefas();
        for (Tarefa t : tarefas) {
        	tableView.getItems().add(t);
		}
	}
	
	private List<Tarefa> getTarefas() {

		// Get the Books
		Gson gson = new Gson();

		String responseTarefasList = service.path("getTarefas")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.get(String.class);

		List<Tarefa> tarefas = Arrays.asList(gson.fromJson(responseTarefasList, Tarefa[].class));
				
		return tarefas;
	}
	
	private void showAddUpdateTarefaStage(Stage primaryStage, Tarefa tarefa) {
		
		Stage stage = new Stage();
		
		stage.setTitle("Adicionar ou atualizar Tarefas - in Modal Mode");
        stage.setX(300);
        stage.setY(300);
        stage.setWidth(300);
        stage.setHeight(300);
        
        GridPane root = new GridPane();
        
        Label lblTitulo = new Label("Titulo");
        root.add(lblTitulo, 0, 0, 1, 1);
        
        TextField txtTitulo = new TextField();
        lblTitulo.setId("lblTitulo");
        root.add(txtTitulo, 1, 0, 1, 1);
        
        Label lblDescricao = new Label("Descricao");
        root.add(lblDescricao, 0, 1, 1, 1);
        
        TextField txtDescricao = new TextField();
        lblDescricao.setId("lblDescricao");
        root.add(txtDescricao, 1, 1, 1, 1);
        
        Label lblDataConclusao = new Label("DataConclusao");
        root.add(lblDataConclusao, 0, 2, 1, 1);
        
        TextField txtDataConclusao = new TextField();
        lblDescricao.setId("lblDataConclusao");
        root.add(txtDataConclusao, 1, 2, 1, 1);
        
        Label lblPrioridade = new Label("Prioridade");
        root.add(lblPrioridade, 0, 3, 1, 1);
        
        TextField txtPrioridade = new TextField();
        lblPrioridade.setId("lblPrioridade");
        root.add(txtPrioridade, 1, 3, 1, 1);
        		

		Button btnSave = new Button("Save");
		Button btnCancel = new Button("Cancel");
		root.add(btnSave, 0, 4, 1, 1);
		root.add(btnCancel, 1, 4, 1, 1);
		
		root.setHgap(5);
		root.setVgap(5);
		
		if (tarefa.getTitulo() != null) {
			txtTitulo.setText(tarefa.getTitulo());
			txtDescricao.setText(tarefa.getDescricao());
			txtDataConclusao.setText(tarefa.getDataConclusao());
			txtPrioridade.setText(tarefa.getPrioridade());	
		}
		
		btnSave.setOnAction(ae -> {
			System.out.println("A guardar... " + tarefa);
			
			tarefa.setTitulo(txtTitulo.getText());
			tarefa.setDescricao(txtDescricao.getText());
			tarefa.setDataConclusao(txtDataConclusao.getText());
			tarefa.setPrioridade(txtPrioridade.getText());
			
			saveData(tarefa);
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
	
	private void saveData(Tarefa tarefa) {
		try {
			
			Response response;
			String message = "Tarefa adicionada com sucesso";
			
			if(tarefa.getTitulo() != null) {
				message = "Tarefa atualizada com sucesso";
				response = service.path("updateTarefa").request(MediaType.APPLICATION_JSON)
						.put(Entity.entity(tarefa, MediaType.APPLICATION_JSON), Response.class);	
			} else {
				message = "Tarefa adicionada com sucesso";
				response = service.path("addTarefa").request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(tarefa, MediaType.APPLICATION_JSON), Response.class);	
			}
			if (response.getStatus() < 200 || response.getStatus() > 299) {			
				showMessage("Failed : HTTP error code : " + response.getStatus(), AlertType.ERROR);
				//throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}else {
				showMessage(message, AlertType.INFORMATION);
			}
		} catch (Exception e) {
			showMessage("Erro ao guardar a tarefa", AlertType.ERROR);
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
        alert.setTitle("Tarefas Application");
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

		alert.setTitle("Delete tarefa warning");
		Optional<ButtonType> result = alert.showAndWait();

		return (result.orElse(closeBtn) == okBtn);
	}
}

