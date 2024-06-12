package org.pampasim.GUI.model;

import javafx.scene.Cursor;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.pampasim.GUI.model.InfoProcessDialog;
import org.pampasim.Os.Process;

import java.util.Optional;

public class ProcessViewModel {

    private Process process;
    private Stage stage;
    public ProcessViewModel() {
        super();
    }
    public void setProcess(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void viewProcessInfo(){
        Dialog<ButtonType> processInfoDialog = new InfoProcessDialog(this.process);
        Optional<ButtonType> processInfoDialogResult = processInfoDialog.showAndWait();
    }

    public void onMouseEnteredHandler(MouseEvent mouseEvent) {
        this.stage.getScene().setCursor(Cursor.HAND);
    }
}
