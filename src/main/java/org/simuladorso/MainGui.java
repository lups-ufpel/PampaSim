package org.simuladorso;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.simuladorso.GUI.controller.FXMLMainAppController;

import java.io.IOException;
import java.net.URL;

public class MainGui extends Application {

    //private FXMLMainAppController mainAppController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene screen;
        FXMLLoader fxmlLoader = new FXMLLoader();
        //fxmlLoader.setLocation(getClass().getResource("/fxml/SimpleFXMLExample.fxml"));
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainApp.fxml"));
        AnchorPane root;
        root = fxmlLoader.load();
        screen = new Scene(root);
        //mainAppController = new FXMLMainAppController();
        //this.mainAppController = fxmlLoader.getController();
        //this.mainAppController.setMainStage(primaryStage);
        primaryStage.setTitle("PampaOS");
        primaryStage.setScene(screen);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}