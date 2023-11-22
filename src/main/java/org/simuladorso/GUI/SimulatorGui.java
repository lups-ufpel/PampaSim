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

    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MenuItem exitMenu;
        Scene screen;

        Thread.setDefaultUncaughtExceptionHandler(SimulatorGui::showError);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainApp.fxml"));
        AnchorPane root = new AnchorPane();

        try {
            root = (AnchorPane) fxmlLoader.load();
        } catch (IOException e) {
            //this.logger.error(e.getMessage());
            //showErrorMessage(e);
            return;
        }

        try {
            screen = new Scene(root);

            this.mainAppController = fxmlLoader.getController();
            this.mainAppController.setMediator(SimulatorGui.mediator);
            this.mainAppController.setMainStage(primaryStage);

            primaryStage.setTitle("Simulador SO");
            primaryStage.setScene(screen);
            primaryStage.show();

        } catch (Exception e) {
            //this.logger.warning(e.getMessage());
            //showErrorMessage(e);
        }
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
