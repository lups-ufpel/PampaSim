package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import org.pampasim.viewModel.AddSpecOrModuleDialogViewModel;
import org.pampasim.dialog.AddModuleDialogService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class AddSpecOrModulesDialogView implements FxmlView<AddSpecOrModuleDialogViewModel>, Initializable {

    @InjectViewModel
    AddSpecOrModuleDialogViewModel viewModel;
    private AddModuleDialogService addModuleDialogService = new AddModuleDialogService();

    @FXML
    public void onSelectModule(ActionEvent actionEvent) {
        viewModel.openAddModuleDialog(addModuleDialogService);
    }

    @FXML
    public void loadSpec(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open specification file");
        File file = fileChooser.showOpenDialog(null);
        viewModel.loadSpec(Paths.get(file.getPath()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
