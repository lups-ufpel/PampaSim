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
    private BorderPane mainFrame;
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaOsGUI.class);

    public PampaOsGUI(Stage mainStage) throws IOException {
        initMainFrameFromFXML();
        configureStage(mainStage);
    }

    private void configureStage(Stage stage) {
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
    private void initMainFrameFromFXML() throws IOException {
        FXMLLoader loader = createLoader("/fxml/pampaos.fxml");
        loadFXML(loader);
    }
    private FXMLLoader createLoader(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if(fxmlUrl == null) {
            throw new IOException("FXML file not found");
        }
        loader.setLocation(fxmlUrl);
        return loader;
    }
    private void loadFXML(FXMLLoader loader) {
        try {
            this.mainFrame = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error loading FXML", e);
        }
    }
}
