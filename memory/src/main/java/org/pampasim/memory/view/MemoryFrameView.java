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
    @FXML
    public Label pageNumberLabel;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
        frameNum.setText(Integer.toString(viewModel.getFrameNum()));

        // Bind page number as text only if it's valid
        pageNumberLabel.textProperty().bind(
                Bindings.when(viewModel.getPageNumber().greaterThanOrEqualTo(0))
                        .then(viewModel.getPageNumber().asString())
                        .otherwise("")
        );

        // Show/hide circle depending on color
        circle.visibleProperty().bind(Bindings.isNotNull(viewModel.getColorProperty()));
        circle.managedProperty().bind(Bindings.isNotNull(viewModel.getColorProperty()));

        // Match pageNumberLabel visibility to circle
        pageNumberLabel.visibleProperty().bind(circle.visibleProperty());
        pageNumberLabel.managedProperty().bind(circle.managedProperty());
    }

}