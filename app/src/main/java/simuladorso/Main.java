package simuladorso;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simuladorso.GUI.Functions;
import simuladorso.VirtualMachine.VirtualMachine;

import java.net.URL;

public class Main extends Application {
    public static void main(String[] args) {
        launch();
    }

    public void run(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        MenuItem exitMenu;
        Scene screen;
        VirtualMachine vm;

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/fxml/mainApp.fxml"));
        VBox root = (VBox) fxmlLoader.load();

        Rectangle2D screenInfo = Screen.getPrimary().getBounds();
        double screenWidth = screenInfo.getWidth();
        double screenHeigth = screenInfo.getHeight();

        exitMenu = (MenuItem) fxmlLoader.getNamespace().get("sairMenu");
        exitMenu.setOnAction(e -> Functions.exit());

        screen = new Scene(root, screenWidth, screenHeigth-70);

        primaryStage.setTitle("Simulador SO");
        primaryStage.setScene(screen);
        primaryStage.show();
    }
}