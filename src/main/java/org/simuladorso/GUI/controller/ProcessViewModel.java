package org.simuladorso.GUI.controller;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.simuladorso.GUI.model.InfoProcessDialog;
import org.simuladorso.Os.Process;

import java.util.Optional;

public class ProcessViewModel {

    private Process process;
    public ProcessViewModel() {
        super();
    }
    public void setProcess(Process process) {
        this.process = process;
    }
    public Process getProcess() {
        return process;
    }
    public void viewProcessInfo(){
        Dialog<ButtonType> processInfoDialog = new InfoProcessDialog(this.process);
        Optional<ButtonType> processInfoDialogResult = processInfoDialog.showAndWait();
    }
}
