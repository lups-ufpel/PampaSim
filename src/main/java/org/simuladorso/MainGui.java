package org.simuladorso;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.simuladorso.GUI.SimulatorGui;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.MediatorDefault;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.Scheduler;
import org.simuladorso.Os.SchedulerFCFS;
import org.simuladorso.VirtualMachine.VmSimple;

public class MainGui extends Application {

    @Override
    public void start(Stage stage) {
        var label = new Label("Hello, JavaFX");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}