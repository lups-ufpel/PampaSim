package simuladorso.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import simuladorso.GUI.controller.FXMLMainAppController;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageBroker;
import simuladorso.MessageBroker.Messager;
import simuladorso.VirtualMachine.VirtualMachine;

import java.io.IOException;

public class SimulatorGui extends Application {
    //private static final Logger logger = new Logger();

    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MenuItem exitMenu;
        Scene screen;
        VirtualMachine vm;

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

            FXMLMainAppController controller = fxmlLoader.getController();
            controller.setMainStage(primaryStage);
            //controller.setLogger(this.logger);

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

    //public Logger getLogger() {
        //return logger;
    //}

    public void subscribeToLogger(Logger logger) {
        //logger.subscribe(this);
    }

    public void receive(Message message) {

    }
}
