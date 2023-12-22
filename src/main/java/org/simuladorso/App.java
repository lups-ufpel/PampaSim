package org.simuladorso;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.SchedulerFCFS;
import org.simuladorso.VirtualMachine.VmSimple;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage){

        try {
            VBox root = new View().root;
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("PampaSim");
            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("Error al cargar la vista");
        }
    }
    public static void main(String[] args) {
        Mediator mediator = Mediator.getInstance();
        mediator.registerComponent(new Os(mediator),Mediator.Component.KERNEL);
        mediator.registerComponent(new VmSimple(1,mediator),Mediator.Component.VM);
        mediator.registerComponent(new SchedulerFCFS(1,mediator),Mediator.Component.SCHEDULER);
        launch();
    }
}
