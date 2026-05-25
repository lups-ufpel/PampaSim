package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pampasim.resources.viewmodel.CreateProcessDialogViewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateProcessDialogView implements FxmlView<CreateProcessDialogViewModel>, Initializable {
    @InjectViewModel
    CreateProcessDialogViewModel viewModel;

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
        viewModel.processStartProperty().bind(startSpinner.getValueFactory().valueProperty());
        viewModel.processDurationProperty().bind(durationSpinner.getValueFactory().valueProperty());
        viewModel.processPriorityProperty().bind(prioritySpinner.getValueFactory().valueProperty());
        colorPicker.valueProperty().bindBidirectional(viewModel.colorHexProperty());

        for (var moduleTuple : viewModel.getModuleInfo().values()) {
            // not sure if we should be creating a new ViewTuple here
            var node = (Node)moduleTuple.initializer().apply(new ViewTuple<>(this, dialogPane, viewModel));
            moduleSection.getChildren().add(node);
        }
    }
}
