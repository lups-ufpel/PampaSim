package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.stage.FileChooser;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import org.pampasim.viewModel.AddSpecOrModulesDialogViewModel;

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
    @FXML public Label modulesFeedback;
    @FXML public Label specFeedback;
    @FXML public CheckBox memoryCheckBox;

    @FXML
    public void loadSpec(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open specification file");
        File file = fileChooser.showOpenDialog(null);
        viewModel.setSpecPath(Optional.of(file.getPath()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
      specFeedback.textProperty().bind(viewModel.getSpecFeedbackStringProperty());
      memoryCheckBox.selectedProperty().bindBidirectional(viewModel.memoryModuleEnabledProperty());
    }
}
