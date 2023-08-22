package simuladorso.GUI.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Observer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class FXMLCpuContainerController implements Initializable, Observer {
    @FXML
    private Label cpuIdLbl;
    @FXML
    private Label processPidLbl;

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
        this.processPidLbl.setText(String.valueOf(this.processPid));
    }

    @Override
    public void update(Object object) {
        LinkedList<Object> params;

        try {
            params = (LinkedList<Object>) object;
            this.setData((Integer) params.get(0), (Boolean) params.get(1), (Integer) params.get(2));
        } catch (Exception e) {
            Logger.getInstance().error(e.getMessage());
            return;
        }
    }
}