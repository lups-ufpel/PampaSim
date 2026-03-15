package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.LoggerContext;
import org.pampasim.Launcher;
import org.pampasim.view.PampaSimView;
import org.pampasim.viewModel.PampaSimViewModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.net.URI;

public class PampaSimGUI extends Application {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimGUI.class);
    private BorderPane mainFrame;

    @Override
    public void start(Stage stage) {
        //Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        Configuration config = ConfigurationFactory.getInstance()
                .getConfiguration(
                        null,
                        null,
                        URI.create("classpath:log4j2.xml")
                );
        Configurator.setLevel(LogManager.getRootLogger(), Launcher.getDebugLevel());

        PampaSimViewModel viewModel = this.initializeMainFrame();
        if (openSetupScreens(viewModel)) {
            this.configureStage(stage);
        }
    }

    private PampaSimViewModel initializeMainFrame() {
        final ViewTuple<PampaSimView, PampaSimViewModel> viewTuple = FluentViewLoader.fxmlView(PampaSimView.class).load();
        this.mainFrame = (BorderPane) viewTuple.getView();

        return viewTuple.getViewModel();
    }

    private boolean openSetupScreens(PampaSimViewModel viewModel) {
        try{
            if (!viewModel.openAddSpecOrModuleDialog()) {
                viewModel.openSettingsDialog();
            }
         } catch (RuntimeException e) {
            // If the thrown exception signals an abort:
            if (e.getMessage().contains("aborted by user")) {
                System.out.println(e.getMessage());
                Platform.exit();
                return false;
            }
        }
        return true;
    }

    private void configureStage(Stage stage) {
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
}
