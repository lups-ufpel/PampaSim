package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.scene.layout.TilePane;
import javafx.fxml.FXML;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;

import org.pampasim.filesystem.viewmodel.FileSystemTabViewModel;
import org.pampasim.filesystem.viewmodel.BlockViewModel;

public class FileSystemTabView implements FxmlView<FileSystemTabViewModel> {

    @InjectViewModel
    private FileSystemTabViewModel viewModel;
    @FXML public TilePane blockTilePane;

    public void initialize(){
      addBlockRecordsListener();
    }

    // maybe listen to tick change instead of block change
    private void addBlockRecordsListener() {
        viewModel.getObservableBlockViewModels().addListener((ListChangeListener<BlockViewModel>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                    blockTilePane.getChildren().clear();
                    viewModel.getObservableBlockViewModels().forEach(vm -> {
                        Parent view = FluentViewLoader.fxmlView(BlockView.class)
                                .viewModel(vm)
                                .load()
                                .getView();
                        blockTilePane.getChildren().add(view);
                    });
                }
            }
        });
    }

}
