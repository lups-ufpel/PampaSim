package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
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
import javafx.scene.control.ButtonType;
import javafx.scene.Cursor;
import javafx.stage.Stage;
import javafx.scene.Scene;

import org.pampasim.filesystem.viewmodel.InodeBlockViewModel;

public class InodeBlockView implements FxmlView<InodeBlockViewModel>{
    @InjectViewModel
    private InodeBlockViewModel viewModel;

    @FXML
    public VBox blockVBox;
    @FXML
    public Label index;

    public void initialize() {
        index.setText(Integer.toString(viewModel.getIndex()));

        blockVBox.setBackground(new Background(
            new BackgroundFill(
                viewModel.getCorrespondingColor(viewModel.getIndex()),
                new CornerRadii(5),
                Insets.EMPTY
            )
        ));


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
