package org.simuladorso;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PampaOsGUI {

    private VBox mainFrame;
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaOsGUI.class);
    public PampaOsGUI(Stage mainStage) throws IOException {
        loadFXML();
        configureStage(mainStage);
    }

    private void configureStage(Stage stage){
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
    private void loadFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/testeHorizontal.fxml"));
        if (loader.getLocation() == null)  {
            throw new IOException("FXML file not found");
        }
        try {
            this.mainFrame = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error loading FXML", e);
        }
    }
}
