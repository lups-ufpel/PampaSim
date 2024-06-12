package org.simuladorso;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class PampaOsGUI {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaOsGUI.class);
    private static final String MAIN_FXML = "/fxml/pampaos.fxml";
    private BorderPane mainFrame;

    public PampaOsGUI(Stage mainStage) throws IOException {
        initializeMainFrame();
        configureStage(mainStage);
    }
    private void configureStage(Stage stage) {
        stage.setTitle("PampaOs");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
    private void initializeMainFrame() throws IOException {
        FXMLLoader loader = createFxmlLoader(MAIN_FXML);
        mainFrame = loader.load();
    }
    private FXMLLoader createFxmlLoader(String fxmlPath) throws IOException {
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if(fxmlUrl == null) {
            throw new IOException("FXML file not found " + fxmlPath);
        }
        return new FXMLLoader(fxmlUrl);
    }
}
