package org.simuladorso.GUI.model;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.simuladorso.GUI.controller.InfoProcessDialogController;
import org.simuladorso.Os.Process;

public class InfoProcessDialog extends Dialog<ButtonType> {
    Label processState;
    Label pid;
    Label burst;
    Label arrival;
    Label priority;
    Label ProcessTypeLabel = new Label("Process Type: ");
    Label pidLabel = new Label("Pid: ");
    Label burstLabel = new Label("Burst: ");
    Label arrivalLabel = new Label("Arrival: ");
    Label priorityLabel = new Label("Priority: ");

    Process proc;
    String color;
    public InfoProcessDialog(Process proc, String color) {
        super();
        this.proc = proc;
        this.color = color;
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
        String style = "-fx-background-color: " + this.color + ";";
        dialogPane.setStyle(style);
        infoController.setProcess(proc);
        this.setDialogPane(dialogPane);
    }
    private void buildUI() {
        processState = new Label();
        pid = new Label();
        burst = new Label();
        arrival = new Label();
        priority = new Label();
        Pane pane = createGridPane();
        System.out.println(this.color);
        String style = "-fx-background-color: " + this.color + ";";
        getDialogPane().setStyle(style);
        getDialogPane().setContent(pane);
        // adding buttons to dialog pane
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);

    }
    private Pane createGridPane(){

        VBox content = new VBox(10);
        GridPane grid = new GridPane();
        grid.setHgap (10);
        grid.setVgap(5);
        grid.add(ProcessTypeLabel, 0, 0);
        grid.add(processState,1,0);
        grid.add(pidLabel, 0,1);
        grid.add(pid,1,1);
        grid.add(burstLabel, 0,2);
        grid.add(burst,1,2);
        grid.add(arrivalLabel, 0,3);
        grid.add(arrival,1,3);
        grid.add(priorityLabel, 0,4);
        grid.add(priority,1,4);
        content.getChildren().addAll(grid);
        return content;
    }
}

