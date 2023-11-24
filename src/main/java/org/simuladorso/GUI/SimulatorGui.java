package org.simuladorso.GUI;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.simuladorso.GUI.controller.FXMLMainAppController;
import org.simuladorso.Mediator.Mediator;

import java.io.IOException;

public class SimulatorGui extends Application {
    private static Mediator mediator;

    private FXMLMainAppController mainAppController;


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

    private static void showError(Thread t, Throwable e) {
        //logger.warning(e.getMessage());
        if (Platform.isFxApplicationThread())
            showErrorDialog(e);
    }

    private static void showErrorDialog(Throwable e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getCause().toString());
        alert.setHeaderText(e.getClass().toGenericString());
        alert.setContentText(e.getCause().toString());
        alert.showAndWait();
    }

    public static void setMediator(Mediator mediator) {
        SimulatorGui.mediator = mediator;
    }

    public FXMLMainAppController getMainAppController() {
        return mainAppController;
    }
}
