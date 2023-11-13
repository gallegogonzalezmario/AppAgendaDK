package es.ieslosmontecillos;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

public class PersonaDetalleViewController {
    // Atributos y componentes FXML
    public static final char CASADO='C';
    public static final char SOLTERO='S';
    public static final char VIUDO='V';
    public static final String CARPETA_FOTOS="Fotos";
    @FXML
    private TextField textFieldNombre;
    @FXML
    private TextField textFieldApellidos;
    @FXML
    private TextField textFieldTelefono;
    @FXML
    private TextField textFieldHijos;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private DatePicker datePickerFechaNacimiento;
    @FXML
    private RadioButton radioButtonSoltero;
    @FXML
    private RadioButton radioButtonCasado;
    @FXML
    private RadioButton radioButtonViudo;
    @FXML
    private TextField textFieldSalario;
    @FXML
    private CheckBox checkBoxJubilado;
    @FXML
    private ComboBox<Provincia> comboBoxProvincia;
    @FXML
    private ImageView imageViewFoto;
    @FXML
    private Pane rootPersonaDetalleView;
    @FXML
    private ToggleGroup rbEstadoCivil;
    private Pane rootAgendaView;
    private TableView<Persona> tableViewPrevio;
    private Persona persona;
    private DataUtil dataUtil;
    private boolean nuevaPersona;

    public void setTableViewPrevio(TableView<Persona> tableViewPrevio){
        this.tableViewPrevio = tableViewPrevio;
    }

    public void setDataUtil(DataUtil dataUtil) { this.dataUtil = dataUtil; }

    public void setRootAgendaView(Pane rootAgendaView){
        this.rootAgendaView = rootAgendaView;
    }

    public void setPersona(Persona persona, Boolean nuevaPersona){
        if (!nuevaPersona){
            this.persona= persona;
        } else {
            this.persona = new Persona();
        }
        this.nuevaPersona=nuevaPersona;
    }

    public void mostrarDatos(){

        // Coloca los datos de una persona seleccionada sobre los campos a rellenar
        textFieldNombre.setText(persona.getNombre());
        textFieldApellidos.setText(persona.getApellidos());
        textFieldTelefono.setText(persona.getTelefono());
        textFieldEmail.setText(persona.getEmail());


        if (persona.getFechaNacimiento() != null){
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date fecNac = formato.parse(persona.getFechaNacimiento());
                LocalDate fechaNac =
                        fecNac.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                datePickerFechaNacimiento.setValue(fechaNac);
            }catch(ParseException err){
                System.err.println("HA OCURRIDO UN ERROR EN LA CONVERSIÓN DE LA FECHA.");
            }
        }

        if (persona.getNumHijos() != null){
            textFieldHijos.setText(persona.getNumHijos().toString());
        }

        // Selecciona uno de los radiobutton del estado civil de la persona
        if (!persona.getEstadoCivil().isEmpty()){
            switch(persona.estadoCivilProperty().getValue().charAt(0)){
                case CASADO:
                    radioButtonCasado.setSelected(true);
                    break;
                case SOLTERO:
                    radioButtonSoltero.setSelected(true);
                    break;
                case VIUDO:
                    radioButtonViudo.setSelected(true);
                    break;
            }
        }

        if (persona.getSalario() != null){
            textFieldSalario.setText(persona.getSalario().toString());
        }

        // Selecciona o deselecciona el estado de jubilación
        if (persona.getJubilado() != null && persona.getJubilado() == 1) {
            checkBoxJubilado.setSelected(true);
        } else {
            checkBoxJubilado.setSelected(false);
        }

        // Establece las provincias en el combobox
        comboBoxProvincia.setItems(dataUtil.getOlProvincias());

        // Selecciona la provincia de la persona seleccionada
        if (persona.getProvincia() != null){
            comboBoxProvincia.setValue(persona.getProvincia());
        }

        // Asigna el nombre de la provincia al seleccionarla
        comboBoxProvincia.setCellFactory(
                (ListView<Provincia> l)-> new ListCell<Provincia>(){
                    @Override
                    protected void updateItem(Provincia provincia, boolean empty){
                        super.updateItem(provincia, empty);
                        if (provincia == null || empty){
                            setText("");
                        } else {
                            setText(provincia.getCodigo()+"-"+provincia.getNombre());
                        }
                    }
                });

        comboBoxProvincia.setConverter(new StringConverter<Provincia>(){

            @Override
            public String toString(Provincia provincia) {
                if (provincia == null) {
                    return null;
                } else {
                    return provincia.getCodigo() + "-" + provincia.getNombre();
                }
            }

            @Override
            public Provincia fromString(String userId){
                return null;
            }
        });

    }


    @FXML
    private void onActionButtonGuardar(ActionEvent event) {
        int numFilaSeleccionada;
        boolean errorFormato = false;


        // Asigna en el objeto Persona los campos y, si faltan campos obligatorios o algunos están mal introducidos, salta una alerta
        persona.setNombre(textFieldNombre.getText());
        persona.setApellidos(textFieldApellidos.getText());
        persona.setTelefono(textFieldTelefono.getText());
        persona.setEmail(textFieldEmail.getText());
        if (!textFieldHijos.getText().isEmpty()){
            try {
                persona.setNumHijos(Integer.valueOf(textFieldHijos.getText()));
            } catch(NumberFormatException ex){
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Número de hijos no válido");
                        alert.showAndWait();
                textFieldHijos.requestFocus();
            }
        }
        if (!textFieldSalario.getText().isEmpty()){
            try {
                Double dSalario =
                        Double.valueOf(textFieldSalario.getText());
                persona.setSalario(dSalario);
            } catch(NumberFormatException ex) {
                errorFormato = true;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Salario no válido");
                        alert.showAndWait();
                textFieldSalario.requestFocus();
            }
        }

        if(checkBoxJubilado.isSelected()){
            Integer jubilado = 1;
            persona.setJubilado(jubilado);
        };

        if (radioButtonCasado.isSelected()){
            persona.setEstadoCivil(String.format("%c",CASADO));
        } else if (radioButtonSoltero.isSelected()){
            persona.setEstadoCivil(String.format("%c",SOLTERO));
        } else if (radioButtonViudo.isSelected()){
            persona.setEstadoCivil(String.format("%c",VIUDO));
        }

        if (datePickerFechaNacimiento.getValue() != null){
            LocalDate localDate = datePickerFechaNacimiento.getValue();
            ZonedDateTime zonedDateTime =
                    localDate.atStartOfDay(ZoneId.systemDefault());
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaComoCadena = sdf.format(date);
            persona.setFechaNacimiento(fechaComoCadena);
        } else {
            persona.setFechaNacimiento(null);
        }


        if (comboBoxProvincia.getValue() != null){
            persona.setProvincia(comboBoxProvincia.getValue());
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,"Debe indicar una provincia");
            alert.showAndWait();
            errorFormato = true;
        }

        if (persona.getFoto() != null){
            String imageFileName=persona.getFoto();
            File file = new File(CARPETA_FOTOS+"/"+imageFileName);
            if (file.exists()){
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } else {
                Alert alert=new Alert(Alert.AlertType.INFORMATION,"No se encuentra la imagen en "+file.toURI().toString());
                alert.showAndWait();
            }
        }



        if(!errorFormato) {
            try {

                if (nuevaPersona){
                    tableViewPrevio.getItems().add(persona);
                    numFilaSeleccionada = tableViewPrevio.getItems().size()- 1;
                    tableViewPrevio.getSelectionModel().select(numFilaSeleccionada);
                    tableViewPrevio.scrollTo(numFilaSeleccionada);
                } else {
                    numFilaSeleccionada=
                            tableViewPrevio.getSelectionModel().getSelectedIndex();
                    tableViewPrevio.getItems().set(numFilaSeleccionada,persona);
                }

                if (nuevaPersona) {
                    dataUtil.addPersona(persona);
                } else {
                    dataUtil.actualizarPersona(persona);
                }

                // Selecciona la persona seleccionada anteriormente en la tabla
                TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada, null);
                tableViewPrevio.getFocusModel().focus(pos);
                tableViewPrevio.requestFocus();

                // Cambia a la tabla anterior
                StackPane rootMain =
                        (StackPane) rootPersonaDetalleView.getScene().getRoot();
                rootMain.getChildren().remove(rootPersonaDetalleView);
                rootAgendaView.setVisible(true);
            }catch (Exception ex){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No se han podido guardar los cambios. "
                                + "Compruebe que los datos cumplen los requisitos");
                        alert.setContentText(ex.getLocalizedMessage());
                alert.showAndWait();

            }
        }

    }

    @FXML
    private void onActionButtonCancelar(ActionEvent event) {
        System.out.println(persona.getEstadoCivil());
        int numFilaSeleccionada =
                tableViewPrevio.getSelectionModel().getSelectedIndex();
        TablePosition pos = new TablePosition(tableViewPrevio, numFilaSeleccionada,null);
        tableViewPrevio.getFocusModel().focus(pos);
        tableViewPrevio.requestFocus();
        StackPane rootMain =
                (StackPane) rootPersonaDetalleView.getScene().getRoot();
        rootMain.getChildren().remove(rootPersonaDetalleView);
        rootAgendaView.setVisible(true);
    }

    // Sirve para mostrar en el imageview al lado del botón examinar una vista previa de cuál va a ser la imagen
    @FXML
    private void onActionButtonExaminar(ActionEvent event){
        File carpetaFotos = new File(CARPETA_FOTOS);
        if (!carpetaFotos.exists()){
            carpetaFotos.mkdir();
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes (jpg, png)", "*.jpg",
                        "*.png"),
                new FileChooser.ExtensionFilter("Todos los archivos","*.*"));
        File file = fileChooser.showOpenDialog(
                rootPersonaDetalleView.getScene().getWindow());
        if (file != null){
            try {
                Files.copy(file.toPath(),new File(CARPETA_FOTOS+
                        "/"+file.getName()).toPath());
                persona.setFoto(file.getName());
                Image image = new Image(file.toURI().toString());
                imageViewFoto.setImage(image);
            } catch (FileAlreadyExistsException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING,"Nombre de archivo duplicado");
                        alert.showAndWait();
            } catch (IOException ex){
                Alert alert = new Alert(Alert.AlertType.WARNING,"No se ha podido guardar la imagen");
                        alert.showAndWait();
            }
        }

    }

    // Sirve para suprimir la foto
    @FXML
    private void onActionSuprimirFoto(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar supresión de imagen");
        alert.setHeaderText("¿Desea SUPRIMIR el archivo asociado a la imagen,\n"
                        + "quitar la foto pero MANTENER el archivo, \no CANCELAR la operación?");
                alert.setContentText("Elija la opción deseada:");
        ButtonType buttonTypeEliminar = new ButtonType("Suprimir");
        ButtonType buttonTypeMantener = new ButtonType("Mantener");
        ButtonType buttonTypeCancel = new ButtonType("Cancelar",
                ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeEliminar, buttonTypeMantener,
                buttonTypeCancel);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeEliminar){
            String imageFileName = persona.getFoto();
            File file = new File(CARPETA_FOTOS + "/" + imageFileName);
            if (file.exists()) {
                file.delete();
            }
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        } else if (result.get() == buttonTypeMantener) {
            persona.setFoto(null);
            imageViewFoto.setImage(null);
        }
    }
}