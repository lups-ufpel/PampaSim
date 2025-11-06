package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.pampasim.view.PampaSimView;
import org.pampasim.viewModel.PampaSimViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PampaSimGUI extends Application {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimGUI.class);
    private BorderPane mainFrame;

    @Override
    public void start(Stage stage) {
        //Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());
        PampaSimViewModel viewModel = this.initializeMainFrame();
        openSetupScreens(viewModel);
        this.configureStage(stage);
    }

    private PampaSimViewModel initializeMainFrame() {
        final ViewTuple<PampaSimView, PampaSimViewModel> viewTuple = FluentViewLoader.fxmlView(PampaSimView.class).load();
        this.mainFrame = (BorderPane) viewTuple.getView();
        
        return viewTuple.getViewModel();
    }

    private void openSetupScreens(PampaSimViewModel viewModel) {
        if (viewModel.openAddSpecOrModuleDialog()) { return; }
        viewModel.openSelectSchedulerDialog();
    }

    private void configureStage(Stage stage) {
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
}
