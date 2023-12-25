package org.simuladorso;

import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.SchedulerFCFS;
import org.simuladorso.VirtualMachine.VmSimple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MainApp extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new NordLight().getUserAgentStylesheet());
        new PampaOsGUI(stage);
    }
    public static void main(String[] args) {
        launch();
    }
}
