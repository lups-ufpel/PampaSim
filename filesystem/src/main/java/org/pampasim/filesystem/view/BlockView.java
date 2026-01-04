package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.Cursor;

import org.pampasim.filesystem.viewmodel.BlockViewModel;
import org.pampasim.filesystem.core.BlockType;

public class BlockView implements FxmlView<BlockViewModel> {
    @InjectViewModel
    private BlockViewModel viewModel;

    @FXML
    public VBox blockVBox;
    @FXML
    public Label number;
    @FXML
    public Label blockLabel;

    public void initialize() {
        number.setText(Integer.toString(viewModel.getNumber()));

        //blockLabel.textProperty().bind(viewModel.getCircleLabel());
        blockLabel.textProperty().set("");

        blockVBox.setCursor(Cursor.HAND);
        //blockVBox.setOnMouseClicked(e -> {
          //viewModel.onClicked();
        //});
  
        blockVBox.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> new Background(
                    new BackgroundFill(
                        BlockType.getCorrespondingColor(viewModel.getType()),
                        new CornerRadii(5),
                        Insets.EMPTY
                    )
                ),
                viewModel.typeProperty()
            )
        );

        blockVBox.setEffect(
            new DropShadow(
                BlurType.GAUSSIAN,
                Color.rgb(0, 0, 0, 0.8),
                4,
                0,
                0,
                0
            )
        );
            }



}
