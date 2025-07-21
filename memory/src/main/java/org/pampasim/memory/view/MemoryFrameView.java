package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.pampasim.memory.viewmodel.MemoryFrameViewModel;

public class MemoryFrameView implements FxmlView<MemoryFrameViewModel> {
    @InjectViewModel
    private MemoryFrameViewModel viewModel;

    @FXML
    public VBox frameVBox;
    @FXML
    public Label frameNum;
    @FXML
    public Circle circle;
    @FXML
    public Label pageNumberLabel;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
        frameNum.setText(Integer.toString(viewModel.getFrameNum()));

        pageNumberLabel.textProperty().bind(
                Bindings.when(viewModel.getPageNumber().greaterThanOrEqualTo(0))
                        .then(viewModel.getPageNumber().asString())
                        .otherwise("")
        );

        circle.visibleProperty().bind(Bindings.isNotNull(viewModel.getColorProperty()));
        circle.managedProperty().bind(Bindings.isNotNull(viewModel.getColorProperty()));

        pageNumberLabel.visibleProperty().bind(circle.visibleProperty());
        pageNumberLabel.managedProperty().bind(circle.managedProperty());

        frameVBox.styleProperty().bind(
                Bindings.when(viewModel.getDirty())
                        .then("-fx-background-color: #dcdcdc; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 4, 0, 0, 0);")
                        .otherwise("-fx-background-color: #f8f8f2; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 4, 0, 0, 0);")
        );

    }

}