package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
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
import javafx.scene.layout.TilePane;
import javafx.scene.Parent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import org.pampasim.filesystem.viewmodel.InodeTableViewModel;
import org.pampasim.filesystem.viewmodel.InodeBlockViewModel;


public class InodeTableView implements FxmlView<InodeTableViewModel>, Initializable {
    @InjectViewModel
    private InodeTableViewModel viewModel;
    @FXML public TilePane inodeTilepane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
      updateTilepaneChildren();
      //addViewModelsListener();
    }

    // maybe listen to tick change instead of block change
    /*
    private void addViewModelsListener() {
        viewModel.getObservableInodeBlockViewModels().addListener((ListChangeListener<InodeBlockViewModel>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                  updateTilepaneChildren();
                }
            }
        });
    }
    */

    private void updateTilepaneChildren() {
        inodeTilepane.getChildren().clear();

        for(InodeBlockViewModel vm : viewModel.getInodeBlockViewModels()){
            Parent view = FluentViewLoader.fxmlView(InodeBlockView.class)
                    .viewModel(vm)
                    .load()
                    .getView();

            inodeTilepane.getChildren().add(view);
        }
    }

}
