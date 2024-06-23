package org.pampasim.gui;

import atlantafx.base.theme.Dracula;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.pampasim.gui.view.PampaSimView;
import org.pampasim.gui.model.SimulationViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PampaSimGUI extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimGUI.class);
    private BorderPane mainFrame;

    @Override
    public void start(Stage stage) {
        Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        this.initializeMainFrame();
        this.configureStage(stage);
    }
    private void initializeMainFrame() {
        final ViewTuple<PampaSimView, SimulationViewModel> viewTuple = FluentViewLoader.fxmlView(PampaSimView.class).load();
        this.mainFrame = (BorderPane) viewTuple.getView();
        PampaSimView pampaSimView = viewTuple.getCodeBehind();
    }
    private void configureStage(Stage stage) {
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
}
