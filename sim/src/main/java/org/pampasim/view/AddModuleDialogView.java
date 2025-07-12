package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import org.pampasim.viewModel.AddModuleDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class AddModuleDialogView implements FxmlView<AddModuleDialogViewModel>, Initializable {

    @InjectViewModel
    AddModuleDialogViewModel viewModel;

    @FXML
    ChoiceBox<String> choiceBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // list of options
        choiceBox.setItems(viewModel.moduleNameProperty());

        // selected value
        choiceBox.valueProperty().bindBidirectional(viewModel.selectedModuleProperty());
    }
}
