package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.scene.layout.TilePane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

import org.pampasim.filesystem.viewmodel.FreeInodesViewModel;
import org.pampasim.filesystem.viewmodel.BitViewModel;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.core.AllocationBitMap;

public class FreeInodesView implements FxmlView<FreeInodesViewModel>, Initializable {

    @InjectViewModel
    private FreeInodesViewModel viewModel;
    @FXML public TilePane bitTilepane;
    @FXML private Rectangle allocatedRect;
    @FXML private Rectangle freeRect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
      updateTilepaneChildren();

      addViewModelsListener();

      allocatedRect.setFill(AllocationBitMap.getCorrespondingColor(AllocationBitMap.allocated));
      freeRect.setFill(AllocationBitMap.getCorrespondingColor(AllocationBitMap.free));
    }

    // maybe listen to tick change instead of block change
    private void addViewModelsListener() {
        viewModel.getBitViewModels().addListener((ListChangeListener<BitViewModel>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                  updateTilepaneChildren();
                }
            }
        });
    }

    private void updateTilepaneChildren() {
    bitTilepane.getChildren().clear();
    viewModel.getBitViewModels().forEach(vm -> {
        Parent view = FluentViewLoader.fxmlView(BitView.class)
                .viewModel(vm)
                .load()
                .getView();
        bitTilepane.getChildren().add(view);
    });

    }

}
