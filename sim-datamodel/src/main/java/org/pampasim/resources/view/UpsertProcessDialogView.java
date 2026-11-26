package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.resources.dialog.UpsertProcessDialogService;
import org.pampasim.resources.viewmodel.UpsertProcessDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class UpsertProcessDialogView implements FxmlView<UpsertProcessDialogViewModel>, Initializable {
    private static final Logger LOGGER = LogManager.getLogger(UpsertProcessDialogView.class);
    @InjectViewModel
    UpsertProcessDialogViewModel viewModel;

    @FXML
    Spinner<Integer> startSpinner;
    @FXML
    Spinner<Integer> durationSpinner;
    @FXML
    Spinner<Integer> prioritySpinner;
    @FXML
    ColorPicker colorPicker;

    @FXML
    public ButtonType okButton;

    @FXML
    private DialogPane dialogPane;

    @Getter
    @FXML
    public VBox moduleSection;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO: handle more invalid inputs
        startSpinner.getValueFactory().setValue(viewModel.getProcessStart());
        durationSpinner.getValueFactory().setValue(viewModel.getProcessDuration());
        prioritySpinner.getValueFactory().setValue(viewModel.getProcessPriority());

        viewModel.processStartProperty().bind(startSpinner.getValueFactory().valueProperty());
        viewModel.processDurationProperty().bind(durationSpinner.getValueFactory().valueProperty());
        viewModel.processPriorityProperty().bind(prioritySpinner.getValueFactory().valueProperty());
        colorPicker.valueProperty().bindBidirectional(viewModel.colorHexProperty());

        // not sure if we should be creating a new ViewTuple here
        var viewTuple = new ViewTuple<>(this, dialogPane, viewModel);
        for (var moduleTuple : viewModel.getModuleInfo().values()) {
            LOGGER.trace("running initializer for module: {}", moduleTuple);
            var node = moduleTuple.initializer().apply(viewTuple);
            moduleSection.getChildren().add(node);
        }
    }
}
