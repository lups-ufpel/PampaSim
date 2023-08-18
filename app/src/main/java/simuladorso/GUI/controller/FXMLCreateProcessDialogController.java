package simuladorso.GUI.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FXMLCreateProcessDialogController implements Initializable {
    @FXML
    private Label processPidLbl;

    @FXML
    private ComboBox<String> processTypeCbx;

    @FXML
    private Button createBtn;

    @FXML
    private Button cancelBtn;

    private Stage dialogStage;
    private boolean createBtnClicked = false;
    private ArrayList<String> params;
    private String[] cbxOptions = {"CPU Bound", "IO Bound", "CPU-IO Bound"};

    public void initialize(URL url, ResourceBundle resourceBundle) {
        processTypeCbx.getItems().addAll(cbxOptions);
        processTypeCbx.setValue("CPU Bound");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setParams(ArrayList<String> params) {
        this.params = params;
    }

    public boolean isCreateBtnClicked() {
        return createBtnClicked;
    }

    public void handleCancelBtn(ActionEvent actionEvent) {
        dialogStage.close();
    }

    public void handleCreateBtn(ActionEvent actionEvent) {
        params.add(processPidLbl.getText());
        if (processTypeCbx.getValue() == null) {
            return;
        }
        params.add(processTypeCbx.getValue());

        createBtnClicked = true;
        dialogStage.close();
    }
}
