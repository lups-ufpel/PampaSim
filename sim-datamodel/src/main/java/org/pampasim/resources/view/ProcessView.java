package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import org.pampasim.resources.viewmodel.ProcessViewModel;

public class ProcessView  implements FxmlView<ProcessViewModel> {

    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML
    Circle circle;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
    }

    @FXML
    public void showInfo(MouseEvent mouseEvent) {
    }
}
