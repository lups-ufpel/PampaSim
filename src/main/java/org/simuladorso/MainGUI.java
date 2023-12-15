package org.simuladorso;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.simuladorso.GUI.View;

import java.io.IOException;

public class MainGUI extends Application {

    @Override
    public void start(Stage stage){

        View viewMain;

        try {
            viewMain = new View();
            Scene scene = new Scene(viewMain.root, 905, 633);
            stage.setScene(scene);
            stage.setTitle("Simulador SO");
            stage.show();
        } catch (IOException e) {
            System.out.println("teste");
            // TODO Auto-generated catch block
        }
    }
    public static void main(String[] args){
        launch();
    }
}
