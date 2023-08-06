package simuladorso.GUI;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simuladorso.VirtualMachine.VirtualMachine;

public class App extends Application {

    /*
    public App(VirtualMachine vm) {
        this.vm = vm;
    }*/

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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PrimeiraInterface.fxml"));
        Parent root = fxmlLoader.load();

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
