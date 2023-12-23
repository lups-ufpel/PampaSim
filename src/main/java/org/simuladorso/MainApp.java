package org.simuladorso;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.SchedulerFCFS;
import org.simuladorso.VirtualMachine.VmSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    @Override
    public void start(Stage stage) {
            Scene scene = createScene();
            configureStage(stage,scene);
            stage.show();
    }
    public void configureStage(Stage stage,Scene scene) {
        stage.setTitle("PampaSim");
        stage.setScene(scene);
    }
    public Scene createScene() {
        VBox root = null;
        try {

            root = new View().root;
        } catch (IOException e) {
            LOGGER.error("Cannot create Scene from root node",e);
        }
        return new Scene(root, 1000, 700);
    }
    public static void initializeComponents(){
        Mediator mediator = Mediator.getInstance();
        mediator.registerComponent(new Os(mediator),Mediator.Component.KERNEL);
        mediator.registerComponent(new VmSimple(1,mediator),Mediator.Component.VM);
        mediator.registerComponent(new SchedulerFCFS(1,mediator),Mediator.Component.SCHEDULER);
    }
    public static void main(String[] args) {
        initializeComponents();
        launch();
    }
}
