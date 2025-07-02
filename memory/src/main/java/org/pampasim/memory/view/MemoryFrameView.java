package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import org.pampasim.memory.viewmodel.MemoryFrameViewModel;

public class MemoryFrameView implements FxmlView<MemoryFrameViewModel> {
    @InjectViewModel
    private MemoryFrameViewModel viewModel;
    @FXML
    public Label frameNum;
    @FXML
    public Circle circle;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
        frameNum.textProperty().bind(viewModel.getFrameNum().asString());
    }
}
