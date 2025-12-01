package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.viewmodel.ProcessViewModel;

public class ProcessView  implements FxmlView<ProcessViewModel> {

    private final Logger LOGGER = LogManager.getLogger(ProcessView.class);
    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML
    Circle circle;
    @FXML
    Label number;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
        number.textProperty().bind(viewModel.getPid().map(PidAllocator.Pid::toString));
    }

    public void showInfo(MouseEvent mouseEvent) {
        if (viewModel.getPid().get() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Processo não foi inicializado!");
            alert.show();
        }

        var processInspectorViewTuple = FluentViewLoader
                .fxmlView(ProcessInspectorView.class)
                .viewModel(viewModel)
                .load();

        Stage stage = new Stage();
        stage.setTitle("Informações do Processo");
        stage.setScene(new Scene(processInspectorViewTuple.getView()));
        stage.setResizable(true);
        stage.show();
    }


}
