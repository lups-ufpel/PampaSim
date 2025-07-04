package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
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
        // Bind circle color
        circle.fillProperty().bind(viewModel.getColorProperty());

        // Set frame number (static text)
        frameNum.setText(Integer.toString(viewModel.getFrameNum()));

        // Hide circle if color is null
        circle.visibleProperty().bind(
                Bindings.isNotNull(viewModel.getColorProperty())
        );
        circle.managedProperty().bind(
                Bindings.isNotNull(viewModel.getColorProperty())
        );
    }
}