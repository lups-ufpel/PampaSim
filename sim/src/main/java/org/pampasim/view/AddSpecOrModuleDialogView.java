package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import org.pampasim.viewModel.AddSpecOrModuleDialogViewModel;
import org.pampasim.viewModel.PampaSimViewModel;
import org.pampasim.SimulatedScenario;
import org.pampasim.dialog.AddModuleDialogService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;

public class AddSpecOrModuleDialogView implements FxmlView<AddSpecOrModuleDialogViewModel>, Initializable {

    @InjectViewModel
    AddSpecOrModuleDialogViewModel viewModel;
    private List<String> modules;
    private AddModuleDialogService addModuleDialogService = new AddModuleDialogService();

    @FXML
    public void onSelectModule(ActionEvent actionEvent) {
        viewModel.openAddModuleDialog(modules, addModuleDialogService);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
