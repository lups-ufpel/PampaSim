package org.simuladorso.GUI.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import org.simuladorso.Os.Process;

public class InfoProcessDialogController {

    public ProgressBar pb;
    public ProgressIndicator pi;
    @FXML
    private Label processState;
    @FXML
    private Label pid;
    @FXML
    private Label burst;
    @FXML
    private Label arrival;
    @FXML
    private Label priority;

    private Process proc;

    // Method to initialize the controller
    public void initialize() {
        // Assuming the proc object is set externally
        // Bind the properties here
        if (proc != null) {
            bindProcessProperties();
        }

    }

    public void setProcess(Process proc) {
        this.proc = proc;
        bindProcessProperties();
    }

    private void bindProcessProperties() {
        // Bind the label text properties to the corresponding properties of the Process object
        processState.textProperty().bind(proc.stateProperty());
        pid.textProperty().bind(proc.pidProperty().asString());
        burst.textProperty().bind(proc.burstProperty().asString());
        arrival.textProperty().bind(proc.arrivalProperty().asString());
        priority.textProperty().bind(proc.priorityProperty().asString());
        pi.progressProperty().bind(proc.burstProperty().divide(proc.burstProperty()));
        pb.progressProperty().bind(proc.burstProperty().divide(proc.burstProperty()));
    }
}
