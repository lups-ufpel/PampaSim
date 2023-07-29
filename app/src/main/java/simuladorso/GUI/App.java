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

public class App extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PrimeiraInterface.fxml"));
        Parent root = fxmlLoader.load();
        Rectangle2D screenInfo = Screen.getPrimary().getBounds();
        double screenWidth = screenInfo.getWidth();
        double screenHeigth = screenInfo.getHeight();

        /*Button createNewButton = (Button) fxmlLoader.getNamespace().get("createNew");
        createNewButton.setOnAction(e -> Functions.mudarTexto(createNewButton));
        */
        MenuItem sairMenu = (MenuItem) fxmlLoader.getNamespace().get("sairMenu");
        sairMenu.setOnAction(e -> Functions.sair());
        
        Scene tela = new Scene(root, screenWidth, screenHeigth-70);
        
        primaryStage.setTitle("Interface do vinizão boladão");
        primaryStage.setScene(tela);
        primaryStage.show();

    }

}
