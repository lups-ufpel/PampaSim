package org.pampasim.gui.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import org.pampasim.gui.viewmodel.CreateProcessDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateProcessDialogView implements FxmlView<CreateProcessDialogViewModel>, Initializable {

    @InjectViewModel
    CreateProcessDialogViewModel viewModel;

    @FXML
    Spinner<Integer> burstSpinner;
    @FXML
    Spinner<Integer> prioritySpinner;
    @FXML
    Spinner<Integer> durationSpinner;
    @FXML
    ColorPicker colorPicker;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindViewValues();
    }

    public void bindViewValues() {
        burstSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.getBurstProperty());
        prioritySpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.getPriorityProperty());
        durationSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.getDurationProperty());
    }
}
