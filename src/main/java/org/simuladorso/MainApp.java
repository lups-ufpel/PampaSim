package org.simuladorso;

import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        new PampaOsGUI(stage);
    }
    public static void main(String[] args) {
        launch();
    }
}
