package org.pampasim;

import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.pampasim.GUI.controllers.PampaSimController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class PampaSimGUI extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimGUI.class);
    private static final String MAIN_FXML = "/fxml/PampaSim.fxml";
    private BorderPane mainFrame;
    private Stage stage;
    private PampaSimController pampaSimController;

    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        this.initializeMainFrame();
        this.configureStage(stage);
    }

    private void initializeMainFrame() throws IOException {
        FXMLLoader loader = createFxmlLoader(MAIN_FXML);
        this.mainFrame = loader.load();
        this.pampaSimController = loader.getController();
    }

    private void configureStage(Stage stage) {
        this.stage = stage;
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
        pampaSimController.setScene(this.stage.getScene());
    }
    private FXMLLoader createFxmlLoader(String fxmlPath) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if(fxmlUrl == null) {
            throw new IOException("FXML file not found " + fxmlPath);
        }
        return new FXMLLoader(fxmlUrl);
    }
}
