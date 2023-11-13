package es.ieslosmontecillos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class AgendaViewController implements Initializable {
    private DataUtil dataUtil;
    private ObservableList<Provincia> olProvincias;
    private ObservableList<Persona> olPersonas;
    private Persona personaSeleccionada;
    @FXML
    private TableView<Persona> tableViewAgenda;
    @FXML
    private TableColumn<Persona,String> columnNombre;
    @FXML
    private TableColumn<Persona,String> columnApellidos;
    @FXML
    private TableColumn<Persona,String> columnEmail;
    @FXML
    private TableColumn<Persona,String> columnProvincia;
    @FXML
    private Button buttonGuardar;
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private VBox vBoxTableView;
    @FXML
    private AnchorPane rootAgendaView;
    @FXML
    private Text textPersonaSeleccionada;
    @FXML
    private Text textErrorGuardar;
    private PersonaDetalleViewController personaDetalleViewController;


    public Text getTextPersonaSeleccionada(){return textPersonaSeleccionada;}
    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }

    public void setOlProvincias(ObservableList<Provincia> olProvincias) {
        this.olProvincias = olProvincias;
    }

    public void setOlPersonas(ObservableList<Persona> olPersonas) {
        this.olPersonas = olPersonas;
    }

    // Accede a la base de datos para añadir los datos de las personas
    public void cargarTodasPersonas(){
        tableViewAgenda.setItems(FXCollections.observableArrayList(olPersonas));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb){

        // Columnas de la tabla para Persona
        // Nombre
        columnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Apellidos
        columnApellidos.setCellValueFactory(new
                PropertyValueFactory<>("apellidos"));

        // Correo
        columnEmail.setCellValueFactory(new
                PropertyValueFactory<>("email"));

        // Provincia.getNombre()
        columnProvincia.setCellValueFactory(
                cellData->{
                    SimpleStringProperty property=new SimpleStringProperty();
                    if (cellData.getValue().getProvincia() != null){
                        property.setValue(cellData.getValue().getProvincia().getNombre());
                    }
                    return property;
                });

        // Eventos
        // Selección fila TableView
        tableViewAgenda.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->{
            personaSeleccionada=newValue;
            if (personaSeleccionada != null){
                textFieldNombre.setText(personaSeleccionada.getNombre());
                textFieldApellidos.setText(personaSeleccionada.getApellidos());
            } else {
                textFieldNombre.setText("");
                textFieldApellidos.setText("");
            }
        });

    }

    @FXML
    private void onActionButtonNuevo(ActionEvent event){
        try{
            if(textPersonaSeleccionada.isVisible())
                textPersonaSeleccionada.setVisible(false);
            personaSeleccionada = new Persona();
            // Cargar la vista de detalle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PersonaDetalleView.fxml"));
            Parent rootDetalleView=fxmlLoader.load();
            personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
            personaDetalleViewController.setRootAgendaView(rootAgendaView);

            personaDetalleViewController.setPersona(personaSeleccionada,true);

            // Ocultar la vista de la lista
            rootAgendaView.setVisible(false);

            //Añadir la vista detalle al StackPane principal para que se muestre
            StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
            personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
            personaDetalleViewController.setDataUtil(dataUtil);
            rootMain.getChildren().add(rootDetalleView);
            personaDetalleViewController.mostrarDatos();
        } catch (IOException ex){
            System.out.println("Error volcado"+ex);}
    }
    @FXML
    private void onActionButtonEditar(ActionEvent event){

        if(personaSeleccionada != null) {
            if(textPersonaSeleccionada.isVisible())
                textPersonaSeleccionada.setVisible(false);
            try {
                // Cargar la vista de detalle
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/PersonaDetalleView.fxml"));
                Parent rootDetalleView = fxmlLoader.load();
                personaDetalleViewController =
                        (PersonaDetalleViewController) fxmlLoader.getController();
                personaDetalleViewController.setRootAgendaView(rootAgendaView);

                personaDetalleViewController.setPersona(personaSeleccionada,false);

                // Ocultar la vista de la lista
                rootAgendaView.setVisible(false);
                personaDetalleViewController = (PersonaDetalleViewController) fxmlLoader.getController();
                personaDetalleViewController.setRootAgendaView(rootAgendaView);

                //Añadir la vista detalle al StackPane principal para que se muestre
                StackPane rootMain = (StackPane) rootAgendaView.getScene().getRoot();
                personaDetalleViewController.setTableViewPrevio(tableViewAgenda);
                personaDetalleViewController.setDataUtil(dataUtil);
                rootMain.getChildren().add(rootDetalleView);
                personaDetalleViewController.mostrarDatos();

            } catch (IOException ex) {
                System.out.println("Error volcado" + ex);
            }
        }else{
            textPersonaSeleccionada.setVisible(true);
        }
    }
    @FXML
    private void onActionButtonSuprimir(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("¿Desea suprimir el siguiente registro?");
        alert.setContentText(personaSeleccionada.getNombre() + " "
                + personaSeleccionada.getApellidos());
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // Acciones a realizar si el usuario acepta
            dataUtil.eliminarPersona(personaSeleccionada);
            tableViewAgenda.getItems().remove(personaSeleccionada);
            tableViewAgenda.getFocusModel().focus(null);
            tableViewAgenda.requestFocus();

        } else {
            // Acciones a realizar si el usuario cancela
            int numFilaSeleccionada=
                    tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);
            TablePosition pos = new TablePosition(tableViewAgenda,
                    numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }
    }

    @FXML
    public void onActionButtonGuardar(ActionEvent actionEvent) {

        if (personaSeleccionada != null){
            if(textErrorGuardar.isVisible())
                textErrorGuardar.setVisible(false);
            personaSeleccionada.setNombre(textFieldNombre.getText());
            personaSeleccionada.setApellidos(textFieldApellidos.getText());

            dataUtil.actualizarPersona(personaSeleccionada);

            int numFilaSeleccionada = tableViewAgenda.getSelectionModel().getSelectedIndex();
            tableViewAgenda.getItems().set(numFilaSeleccionada,personaSeleccionada);

            TablePosition pos = new TablePosition(tableViewAgenda,numFilaSeleccionada,null);
            tableViewAgenda.getFocusModel().focus(pos);
            tableViewAgenda.requestFocus();
        }else{
            textErrorGuardar.setVisible(true);
        }
    }
}
