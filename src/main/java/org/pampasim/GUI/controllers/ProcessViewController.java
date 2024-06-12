package org.pampasim.GUI.controllers;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.pampasim.GUI.model.SimulationViewModel;
import org.pampasim.GUI.model.InfoProcessDialog;
import org.pampasim.Os.Process;

import java.util.Optional;

public class ProcessViewController {
    @FXML
    private Label pidLabel;
    @FXML
    private Circle processCircle;
    @FXML
    private ProgressBar processBar;
    private Property<ProgressBar> progressProperty = new SimpleObjectProperty<>();
    Process process;
    VBox rootElement;
    public SimulationViewModel simulationViewModel;
    private Scene scene;

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
        this.processBar.progressProperty().set(progress);
        //this.progressProperty.getValue().setProgress(progress);
    }
    public Circle getProcessCircle() {
        return processCircle;
    }

    public ProgressBar getProcessBar() {
        return processBar;
    }

    public Property<Circle> circleForTableView() {
        Property<Circle> newCircle = new SimpleObjectProperty<>();
        Circle c = new Circle(10, this.processCircle.getFill());
        newCircle.setValue(c);
        return newCircle;
    }
    public Property<ProgressBar> progressForTableView() {
        this.progressProperty.setValue(this.processBar);
        return this.progressProperty;
    }

    // Action listeners
    public void showProcessInfo(MouseEvent action) {
        simulationViewModel.showProcessInfo(this.process);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void viewProcessInfo(){
        Dialog<ButtonType> processInfoDialog = new InfoProcessDialog(this.process);
        Optional<ButtonType> processInfoDialogResult = processInfoDialog.showAndWait();
    }

    public void onMouseEnteredHandler(MouseEvent mouseEvent) {
        this.scene.setCursor(Cursor.HAND);
    }

    public void onMouseExitedHandler(MouseEvent mouseEvent) {
        this.scene.setCursor(Cursor.DEFAULT);
    }
}
