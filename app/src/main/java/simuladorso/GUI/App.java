package simuladorso.GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainApp.fxml"));
        Parent root = fxmlLoader.load();
        System.out.println(root.getChildrenUnmodifiable());

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
