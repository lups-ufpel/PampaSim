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

import java.util.Optional;

public class ProcessView implements FxmlView<ProcessViewModel> {

    private final Logger LOGGER = LogManager.getLogger(ProcessView.class);
    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML
    Circle circle;
    @FXML
    Label number;

    private Optional<Stage> inspectorView = Optional.empty();

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
        number.textProperty().bind(viewModel.getPid().map(PidAllocator.Pid::toString));
    }

    public void showInfo(MouseEvent mouseEvent) {
        if (inspectorView.isPresent()) {
            var stage = inspectorView.get();
            if (stage.isShowing()) {
                stage.requestFocus();
                return;
            }
        }
        var processInspectorViewTuple = FluentViewLoader
                .fxmlView(ProcessInspectorView.class)
                .viewModel(viewModel)
                .load();

        Stage stage = new Stage();
        stage.setTitle("Informações do Processo");
        stage.setScene(new Scene(processInspectorViewTuple.getView()));
        stage.setResizable(true);
        inspectorView = Optional.of(stage);
        viewModel.subscribe("CloseInspectors", (_a, b) -> {
            inspectorView = Optional.empty();
        });
        stage.show();
    }
}
