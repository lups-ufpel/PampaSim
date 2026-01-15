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
import javafx.geometry.Pos;
import javafx.scene.paint.Paint;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;

import org.pampasim.filesystem.viewmodel.FileSystemTabViewModel;
import org.pampasim.filesystem.viewmodel.BlockViewModel;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.LegendEntry;

public class FileSystemTabView implements FxmlView<FileSystemTabViewModel>, Initializable {

    @InjectViewModel
    private FileSystemTabViewModel viewModel;
    @FXML public TilePane blockTilepane;
    @FXML private VBox legendBox;
    @FXML private Rectangle mbrRect;
    @FXML private Rectangle initializationRect;
    @FXML private Rectangle superBlockRect;
    @FXML private Rectangle freeBlocksBitMapRect;
    @FXML private Rectangle freeInodesBitMapRect;
    @FXML private Rectangle inodeTableRect;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
      updateTilepaneChildren();

      addViewModelsListener();

      mbrRect.setFill(BlockType.getCorrespondingColor(BlockType.MBR));
      initializationRect.setFill(BlockType.getCorrespondingColor(BlockType.INITIALIZATION));
      superBlockRect.setFill(BlockType.getCorrespondingColor(BlockType.SUPERBLOCK));
      freeBlocksBitMapRect.setFill(BlockType.getCorrespondingColor(BlockType.FREE_BLOCKS_BITMAP));
      freeInodesBitMapRect.setFill(BlockType.getCorrespondingColor(BlockType.FREE_INODES_BITMAP));
      inodeTableRect.setFill(BlockType.getCorrespondingColor(BlockType.INODE_TABLE));

      for(LegendEntry e : viewModel.getLegendEntries()){
        addLegendEntry(e.isDirectory(), e.text(), e.color());
      }
      viewModel.getLegendEntries().addListener(
          (ListChangeListener<LegendEntry>) change -> {
              while (change.next()) {
                  if (change.wasAdded()) {
                      for (LegendEntry e : change.getAddedSubList()) {
                          addLegendEntry(e.isDirectory(), e.text(), e.color());
                      }
                  }
              }
          }
      );
    }

    // maybe listen to tick change instead of block change
    private void addViewModelsListener() {
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

    private void addLegendEntry(Boolean isDirectory, String text, Paint color) {
        String labelText = ((isDirectory) ? "Diretório: " : "Arquivo: ") + text;
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        label.setPrefWidth(250);
    
        Rectangle rect = new Rectangle(16, 16);
        rect.setFill(color);
    
        HBox row = new HBox(0);
        row.getChildren().addAll(label, rect);
    
        legendBox.getChildren().add(row);
    }

}
