package org.pampasim.gui;

import atlantafx.base.theme.CupertinoLight;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.pampasim.gui.view.PampaSimView;
import org.pampasim.gui.viewmodel.SimulationViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PampaSimGUI extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimGUI.class);
    private BorderPane mainFrame;

    @Override
    public void start(Stage stage) {
        //Application.setUserAgentStylesheet(new Dracula().getUserAgentStylesheet());
        Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        this.initializeMainFrame();
        this.configureStage(stage);
    }

    private void initializeMainFrame() {
        ProcessScope processScope = new ProcessScope();
        SchedulerDialogScope schedulerDialogScope = new SchedulerDialogScope();
        final ViewTuple<PampaSimView, SimulationViewModel> viewTuple = FluentViewLoader.fxmlView(PampaSimView.class)
                .providedScopes(processScope,schedulerDialogScope).load();
        this.mainFrame = (BorderPane) viewTuple.getView();
    }

    private void configureStage(Stage stage) {
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
}

