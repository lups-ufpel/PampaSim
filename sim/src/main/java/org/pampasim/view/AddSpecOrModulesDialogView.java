package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.stage.FileChooser;
import org.pampasim.viewModel.AddSpecOrModulesDialogViewModel;
import org.pampasim.dialog.AddModulesDialogService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ResourceBundle;

public class AddSpecOrModulesDialogView implements FxmlView<AddSpecOrModulesDialogViewModel>, Initializable {

    @InjectViewModel
    AddSpecOrModulesDialogViewModel viewModel;
    private AddModulesDialogService addModulesDialogService = new AddModulesDialogService();

    @FXML
    public void onSelectModule(ActionEvent actionEvent) {
        viewModel.openAddModuleDialog(addModulesDialogService);
    }

    @FXML
    public void loadSpec(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open specification file");
        File file = fileChooser.showOpenDialog(null);
        viewModel.setSpecPath(Optional.of(file.getPath()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
