package es.ieslosmontecillos;

import com.gluonhq.charm.glisten.mvc.View;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
public class InicioController {
    @FXML
    private View inicio;
    @FXML
    private Label label;
    private DataUtil dataUtil;
    ObservableList olProv;
    ObservableList olPers;
    private Pane rootMain = new Pane();
    @FXML
    private VBox vBox;



    @Deprecated
    public void iniciaApp(Event event){
        try{
            FXMLLoader fxmlLoader = new
                    FXMLLoader(getClass().getResource("fxml/AgendaView.fxml"));
            Pane rootAgendaView = fxmlLoader.load();
            rootMain.getChildren().add(rootAgendaView);
            AgendaViewController agendaViewController =
                    fxmlLoader.getController();
            agendaViewController.setDataUtil(dataUtil);
            agendaViewController.setOlProvincias(olProv);
            agendaViewController.setOlPersonas(olPers);
            agendaViewController.cargarTodasPersonas();
            inicio.setVisible(false);
        } catch (IOException e) {
            System.out.println("IOException: " + e);
        }
    }


    public void setRootMain(Pane rootMain) {
        this.rootMain = rootMain;
    }
    public void setDataUtil(DataUtil dataUtil) {
        this.dataUtil = dataUtil;
    }
    public void setOlProv(ObservableList olProv) {
        this.olProv = olProv;
    }
    public void setOlPers(ObservableList olPers) {
        this.olPers = olPers;
    }

}

