package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import org.pampasim.viewModel.AddSpecOrModuleDialogViewModel;
import org.pampasim.viewModel.PampaSimViewModel;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AddSpecOrModuleDialogView implements FxmlView<AddSpecOrModuleDialogViewModel>, Initializable {

    @InjectViewModel
    AddSpecOrModuleDialogViewModel viewModel;
    private SimulatedScenario simulatedScenario; // only info needed for module part of dialogue

    @FXML
    public void onSelectModule(ActionEvent actionEvent) {
        viewModel.openAddSpecOrModuleDialog();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
