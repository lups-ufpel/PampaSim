package simuladorso.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simuladorso.GUI.controller.FXMLMainAppController;
import simuladorso.Logger.Log;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.Messager;
import simuladorso.VirtualMachine.VirtualMachine;

import java.io.IOException;

public class SimulatorGui extends Application implements Messager {
    private Logger logger;

    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MenuItem exitMenu;
        Scene screen;
        VirtualMachine vm;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainApp.fxml"));
        AnchorPane root = new AnchorPane();
        try {
            root = (AnchorPane) fxmlLoader.load();
        } catch (IOException e) {
            showErrorMessage(e);
            return;
        }

        //Rectangle2D screenInfo = Screen.getPrimary().getBounds();
        //double screenWidth = screenInfo.getWidth();
        //double screenHeigth = screenInfo.getHeight();

        //exitMenu = (MenuItem) fxmlLoader.getNamespace().get("sairMenu");
        //exitMenu.setOnAction(e -> Functions.exit());
        try {
        //screen = new Scene(root, screenWidth, screenHeigth-70);
            screen = new Scene(root);
        //root.setPrefWidth(screenWidth);
        //root.setPrefHeight(screenHeigth);

            FXMLMainAppController controller = fxmlLoader.getController();
            controller.setMainStage(primaryStage);

            primaryStage.setTitle("Simulador SO");
            primaryStage.setScene(screen);
            primaryStage.show();
        } catch (Exception e) {
            showErrorMessage(e);
            return;
        }
    }

    private void showErrorMessage(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getClass().toString());
        alert.setHeaderText(e.getClass().toGenericString());
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void subscribeToLogger(Logger logger) {
        //logger.subscribe(this);
    }

    public void receive(Message message) {

    }
}
