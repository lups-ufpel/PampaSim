package org.simuladorso;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.simuladorso.GUI.SimulationViewModel;
import org.simuladorso.GUI.controller.ProcessViewModel;
import org.simuladorso.GUI.model.InfoProcessDialog;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;

import java.io.IOException;
import java.util.Optional;

public class ProcessView {

    @FXML
    private Label pidLabel;
    @FXML
    private Circle processCircle;
    @FXML
    private ProgressBar processBar;
    Process process;

    VBox rootElement;
    public SimulationViewModel simulationViewModel;
    public void setRootElement(VBox rootElement) {
        this.rootElement = rootElement;
    }
    public void setCircleColor(Color color) {
        this.processCircle.setFill(color);
    }
    public void setViewModel(SimulationViewModel simulationViewModel) {
        this.simulationViewModel = simulationViewModel;
    }

    public void setProcess(Process process) {
        this.process = process;
    }
    public Process getProcess() {
        return process;
    }
    public void setExecutionProgress(double progress) {
        this.processBar.setProgress(progress);
    }
    public void showProcessInfo(MouseEvent action) {
        simulationViewModel.showProcessInfo(this.process);
    }
}
