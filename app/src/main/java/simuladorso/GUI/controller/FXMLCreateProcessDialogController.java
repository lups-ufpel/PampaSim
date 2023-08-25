package simuladorso.GUI.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import simuladorso.Mediator.Mediator;
import simuladorso.Mediator.MediatorAction;

import java.net.URL;
import java.util.List;
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
    private List<String> params;
    private String[] cbxOptions = {"CPU Bound", "IO Bound", "CPU-IO Bound"};
    private Mediator mediator;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        processTypeCbx.getItems().addAll(cbxOptions);
        processTypeCbx.setValue("CPU Bound");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setParams(List<String> params) {
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

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
        this.processPidLbl.setText(String.valueOf((Integer) mediator.invoke(MediatorAction.GET_AVAILABLE_PID)));
    }
}
