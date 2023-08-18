package simuladorso.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FXMLMainAppController implements Initializable {
    @FXML
    ScrollPane cpusScrollPane;
    @FXML
    ScrollPane processesScrollPane;
    @FXML
    Pane processInfoPane;
    @FXML
    Button createProcessBtn;

    // Sliders
    @FXML
    Slider clockSlider;
    @FXML
    Slider quantumSlider;
    @FXML
    Slider waitTimeSlider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public boolean showFXMLCreateProcessDialog(ArrayList<String> params) throws IOException {
        System.out.println("Clicked");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/createProcessDialog.fxml"));
        AnchorPane dialog = (AnchorPane) loader.load();

        Stage stage = new Stage();
        stage.setTitle("Create new process");
        Scene scene = new Scene(dialog);
        stage.setScene(scene);

        FXMLCreateProcessDialogController controller = loader.getController();
        controller.setDialogStage(stage);
        controller.setParams(params);

        stage.showAndWait();

        return controller.isCreateBtnClicked();
    }

    public void handleCreateProcessBtn(ActionEvent actionEvent) throws IOException {
        ArrayList<String> params = new ArrayList<>();
        boolean buttonConfirmarClicked = showFXMLCreateProcessDialog(params);
        if (buttonConfirmarClicked) {
            System.out.println("OK");
        }
    }
}
