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

import org.pampasim.filesystem.viewmodel.FileSystemTabViewModel;
import org.pampasim.filesystem.viewmodel.BlockViewModel;
import org.pampasim.filesystem.core.BlockType;

public class FileSystemTabView implements FxmlView<FileSystemTabViewModel>, Initializable {

    @InjectViewModel
    private FileSystemTabViewModel viewModel;
    @FXML public TilePane blockTilepane;
    @FXML private Rectangle mbrRect;
    @FXML private Rectangle superBlockRect;
    @FXML private Rectangle freeBlocksBitMapRect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
      updateTilepaneChildren();

      addBlockRecordsListener();

      mbrRect.setFill(BlockType.getCorrespondingColor(BlockType.MBR));
    }

    // maybe listen to tick change instead of block change
    private void addBlockRecordsListener() {
        viewModel.getObservableBlockViewModels().addListener((ListChangeListener<BlockViewModel>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                  updateTilepaneChildren();
                }
            }
        });
    }

    private void updateTilepaneChildren() {
    blockTilepane.getChildren().clear();
    viewModel.getObservableBlockViewModels().forEach(vm -> {
        Parent view = FluentViewLoader.fxmlView(BlockView.class)
                .viewModel(vm)
                .load()
                .getView();
        blockTilepane.getChildren().add(view);
    });

    }

}
