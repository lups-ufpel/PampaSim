package org.pampasim.GUI.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLCoreContainerController implements Initializable {
    @FXML
    private Label cpuIdLbl;
    @FXML
    private Label processIdLbl;

    private int cpuId;
    private boolean running;
    private int processPid;

    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setData(int cpuId, boolean running, int processPid) {
        this.cpuId = cpuId;
        this.cpuIdLbl.setText(String.valueOf(this.cpuId));

        this.running = running;
        this.processPid = processPid;

        if (this.running) {
            this.processIdLbl.setText(String.valueOf(this.processPid));
            this.running = true;
        } else {
            this.processIdLbl.setText("-");
            this.running = false;
        }
    }
}