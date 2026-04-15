package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.resources.viewmodel.ProcessViewModel;

public class ProcessInspectorView implements FxmlView<ProcessViewModel> {
    private final Logger LOGGER = LogManager.getLogger(ProcessInspectorView.class);
    @InjectViewModel
    private ProcessViewModel viewModel;
    @FXML
    private BorderPane mainBorderPane;

    public void initialize() {
        var pcbView = FluentViewLoader
                .fxmlView(PCBView.class)
                .viewModel(viewModel)
                .load().getView();
        mainBorderPane.setCenter(pcbView);

        // auto-close deleted processes
        viewModel.subscribe("CloseInspectors", (_a,_b) -> {
            close();
        });
    }

    public void delete(ActionEvent actionEvent) {
        viewModel.delete(); // publishes CloseInspectors
        LOGGER.debug("called delete on {}", this);
    }

    public void edit(ActionEvent actionEvent) {
        close();
        viewModel.edit();
        LOGGER.debug("called edit on {}", this);
    }

    public void ok(ActionEvent actionEvent) {
        LOGGER.debug("called ok on {}", this);
        close();
    }

    public void close() {
        Stage stage = (Stage) (this.mainBorderPane.getScene()).getWindow();
        stage.close();
    }
}
