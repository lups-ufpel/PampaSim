package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.pampasim.filesystem.viewmodel.BlockViewModel;

public class BlockView implements FxmlView<BlockViewModel> {
    @InjectViewModel
    private BlockViewModel viewModel;

    @FXML
    public VBox blockVBox;
    @FXML
    public Label number;
    @FXML
    public Label pageNumberLabel;
    @FXML
    public Circle circle;

    public void initialize() {
        circle.fillProperty().bind(viewModel.getColorProperty());
        //number.setText(Integer.toString(viewModel.getNumber()));
        number.setText("1");

        circle.visibleProperty().bind(Bindings.isNotNull(viewModel.getColorProperty()));
        circle.managedProperty().bind(Bindings.isNotNull(viewModel.getColorProperty()));

        pageNumberLabel.setText("test");
        pageNumberLabel.visibleProperty().bind(circle.visibleProperty());
        pageNumberLabel.managedProperty().bind(circle.managedProperty());

        blockVBox.setStyle("-fx-background-color: #dcdcdc; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 4, 0, 0, 0);");

    }

}
