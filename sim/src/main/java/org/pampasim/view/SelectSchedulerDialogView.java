package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import org.pampasim.viewModel.SelectSchedulerDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectSchedulerDialogView implements FxmlView<SelectSchedulerDialogViewModel>, Initializable {

    @InjectViewModel
    SelectSchedulerDialogViewModel viewModel;

    @FXML
    ChoiceBox<String> choiceBox;
    @FXML
    CheckBox checkBox;
    @FXML
    Spinner<Integer> spinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // list of options
        choiceBox.setItems(viewModel.schedulerNameProperty());

        // selected value
        choiceBox.valueProperty().bindBidirectional(viewModel.selectedSchedulerProperty());

        // pre-emptive flag
        checkBox.selectedProperty().bindBidirectional(viewModel.preemptiveProperty());

        // quantum
        viewModel.quantumProperty().bind(spinner.getValueFactory().valueProperty());
    }
}
