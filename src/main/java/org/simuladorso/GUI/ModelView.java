package org.simuladorso.GUI;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.simuladorso.GUI.model.InfoProcessDialog;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;
import org.simuladorso.Utils.RGBtoHEX;

public class ModelView {
    public Button BtnStartSim;
    public Button BtnFinishSim;
    public Button BtnCreateProc;
    public VBox CpuContainer1;
    public VBox CpuContainer2;
    public HBox NewList;
    public HBox ReadyList;
    Timeline animation;
    public ModelView() {
        Mediator.getInstance().registerComponent(this, Mediator.Component.GUI);
    }

    @FXML
    private void createProcess(){
        Mediator.getInstance().send(this, Mediator.Action.CREATE);
    }
    public void removeProcessFromRunningList(Circle circleToRemove){
        // check if circleToRemove is in runningProcesses
        if(!CpuContainer1.getChildren().contains(circleToRemove)){
            throw new IllegalStateException("Circle to remove must be in runningProcesses");
        }
        CpuContainer1.getChildren().remove(circleToRemove);
        //runningProcesses.getChildren().remove(circleToRemove);
    }
    public void addProcessToRunningList(Circle circle){
        CpuContainer1.getChildren().add(circle);
    }
    public void addProcessToReadyList(Circle circle){
        ReadyList.getChildren().add(circle);
    }

    public void addCircleToCreatedProcessList(Circle circle){ NewList.getChildren().add(circle);}

    private final Service<Void> createCircleCommand = new Service<Void>() {
        @Override
        protected Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() {

                    animation = new Timeline(new KeyFrame(Duration.millis(500), e -> Mediator.getInstance().send(this, Mediator.Action.RUN)));
                    animation.setCycleCount(Animation.INDEFINITE); // loop forever
                    animation.play();
                    return null;
                }
            };
        }
    };
    @FXML
    private void startSim() {
        createCircleCommand.restart();
    }
    @FXML
    private void finishSim() {
        animation.pause();
    }
    public Circle createCircle(Color color) {
        Circle c = new Circle(35,color);
        c.setOnMouseClicked((evt) -> {
            Mediator.getInstance().send(c, Mediator.Action.VISUALIZE);
        });
        return c;
    }

    public void showProcessInfo(Process process, Color col) {
        // print colors
        String t = RGBtoHEX.rgbToHex((int)(col.getRed()*250), (int)(col.getGreen()*250), (int)(col.getBlue()*250));
        Dialog<ButtonType> processInfoDialog = new InfoProcessDialog(process, t);
        processInfoDialog.showAndWait();
    }
}