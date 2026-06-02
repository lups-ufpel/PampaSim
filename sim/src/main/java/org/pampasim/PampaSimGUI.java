package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import org.pampasim.view.PampaSimView;
import org.pampasim.viewModel.PampaSimViewModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PampaSimGUI extends Application {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimGUI.class);
    private BorderPane mainFrame;

    @Override
    public void start(Stage stage) {
        //Application.setUserAgentStylesheet(new CupertinoLight().getUserAgentStylesheet());

        Configurator.setLevel(LogManager.getRootLogger(), Launcher.getDebugLevel());
        org.pampasim.events.EventManager.initialize();
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
            else {
                showExceptionDialog(e);
            }
        }
        return true;
    }

    // LLM generated
    private void showExceptionDialog(Exception ex) {
        // 1. Create a standard Error Alert
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Caught");
        alert.setHeaderText("An unexpected error occurred");
        alert.setContentText(ex.getMessage());

        // 2. Extract the stack trace into a String
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        // 3. Create a non-editable, wrapping TextArea for the text
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // Allow the text area to expand infinitely within the dialog pane
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        // 4. Arrange the label and text area in a layout container
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("The exception stacktrace was:"), 0, 0);
        expContent.add(textArea, 0, 1);

        // 5. Set the layout container as the expandable content of the dialog
        alert.getDialogPane().setExpandableContent(expContent);

        // 6. Display the modal dialog window
        alert.showAndWait();
    }

    private void configureStage(Stage stage) {
        stage.setTitle("PampaSim");
        stage.setScene(new Scene(mainFrame, 1000, 700));
        stage.show();
    }
}
