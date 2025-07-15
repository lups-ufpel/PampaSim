package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.resources.viewmodel.ProcessViewModel;

public class ProcessView  implements FxmlView<ProcessViewModel> {

    private final Logger LOGGER = LogManager.getLogger(ProcessView.class);
    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML
    Circle circle;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
    }

    public void showInfo(MouseEvent mouseEvent) {
        if (viewModel.getPid() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aviso");
            alert.setHeaderText(null);
            alert.setContentText("Processo não foi inicializado!");
            alert.showAndWait();
            return;
        }

        var pcbViewTuple = FluentViewLoader
                .fxmlView(PCBView.class)
                .viewModel(viewModel)
                .load();

        Stage stage = new Stage();
        stage.setTitle("Informações do Processo");
        stage.setScene(new Scene(pcbViewTuple.getView()));
        stage.setResizable(false);
        stage.show();
    }


}
