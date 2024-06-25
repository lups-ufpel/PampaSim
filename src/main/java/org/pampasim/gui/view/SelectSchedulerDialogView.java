package org.pampasim.gui.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import org.pampasim.gui.viewmodel.SelectSchedulerDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectSchedulerDialogView implements FxmlView<SelectSchedulerDialogViewModel>, Initializable {

    @InjectViewModel
    SelectSchedulerDialogViewModel viewModel;
    @FXML
    ChoiceBox<String> schedulerChoiceBox;
    @FXML
    CheckBox preemptionCheckBox;
    @FXML
    Spinner<Integer> quantumSpinner;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schedulerChoiceBox.getItems().add("FCFS");
        schedulerChoiceBox.getItems().add("SJF");
        schedulerChoiceBox.getItems().add("Round Robin");
        schedulerChoiceBox.getItems().add("Priority");
        preemptionCheckBox.setSelected(false);
        bindViewModel();
    }
    public void bindViewModel() {
        viewModel.getSchedulerNameProperty().bind(schedulerChoiceBox.valueProperty());
        viewModel.getPreemptionProperty().bind(preemptionCheckBox.selectedProperty());
        quantumSpinner.getValueFactory().valueProperty().bindBidirectional(viewModel.getQuantumProperty());
    }
}
