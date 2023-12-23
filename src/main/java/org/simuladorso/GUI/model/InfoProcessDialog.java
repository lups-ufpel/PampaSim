package org.simuladorso.GUI.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import org.simuladorso.GUI.controller.InfoProcessDialogController;
import org.simuladorso.Os.Process;

public class InfoProcessDialog extends Dialog<ButtonType> {
    Process proc;
    public InfoProcessDialog(Process proc) {
        super();
        this.proc = proc;
        this.setTitle("Process Info");
        this.initModality(Modality.NONE);
        FXMLLoader loaderDialog = new FXMLLoader(getClass().getResource("/fxml/InfoProcessDialog.fxml"));
        DialogPane dialogPane = null;
        InfoProcessDialogController infoController = null;
        try {
            dialogPane = loaderDialog.load();
            infoController = loaderDialog.getController();

        } catch (Exception e) {
            System.out.println("oi");
        }
        assert infoController != null;
        assert dialogPane != null;
        infoController.setProcess(proc);
        this.setDialogPane(dialogPane);
    }
}

