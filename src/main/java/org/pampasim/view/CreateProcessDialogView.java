package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import org.pampasim.viewModel.CreateProcessDialogViewModel;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindViewValues();
    }

    public void bindViewValues() {
        viewModel.getStartProperty().bind(startSpinner.valueProperty());
        viewModel.getDurationProperty().bind(durationSpinner.valueProperty());
        viewModel.getPriorityProperty().bind(prioritySpinner.valueProperty());
        viewModel.getColorProperty().setValue(colorPicker.getValue().toString());
        colorPicker.setOnAction(this::handleColorPickerAction);
    }
    private void handleColorPickerAction(ActionEvent event) {
        Color selectedColor = colorPicker.getValue();
        StringProperty colorProperty = new SimpleStringProperty(selectedColor.toString());
        viewModel.getColorProperty().bind(colorProperty);
    }
}
