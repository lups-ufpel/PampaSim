package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.paint.Color;
import org.pampasim.viewModel.CreateProcessDialogViewModel;
import org.pampasim.viewModel.EditProcessDialogViewModel;
import org.pampasim.viewModel.ProcessViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class EditProcessDialogView implements FxmlView<EditProcessDialogViewModel>, Initializable {

    @InjectViewModel
    EditProcessDialogViewModel viewModel;

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
        viewModel.processStartProperty().bind(startSpinner.getValueFactory().valueProperty());
        viewModel.processDurationProperty().bind(durationSpinner.getValueFactory().valueProperty());
        viewModel.processPriorityProperty().bind(prioritySpinner.getValueFactory().valueProperty());
        colorPicker.valueProperty().bindBidirectional(viewModel.colorHexProperty());
    }
    public void setProcessData(int start, int duration, int priority, ObjectProperty<Color> color) {
        startSpinner.getValueFactory().setValue(start);
        durationSpinner.getValueFactory().setValue(duration);
        prioritySpinner.getValueFactory().setValue(priority);
        colorPicker.setValue(color.getValue());
    }
}
