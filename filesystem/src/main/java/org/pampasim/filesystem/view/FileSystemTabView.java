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
import java.util.List;
import java.util.ArrayList;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import org.pampasim.filesystem.viewmodel.FileSystemTabViewModel;
import org.pampasim.filesystem.viewmodel.BlockViewModel;
import org.pampasim.filesystem.viewmodel.FATViewModel;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.LegendEntry;
import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.resources.filesystem.config.FileSystemConfig;

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
    @FXML private HBox freeInodesLegend;
    @FXML private HBox inodeTableLegend;
    @FXML private HBox freeBlocksLegend;
    @FXML private Button openFATButton;

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

      if (FileSystemConfig.getAllocationScheme() != AllocationScheme.INODES) {
          legendBox.getChildren().removeAll(
              freeInodesLegend,
              inodeTableLegend
          );
      }

      if (FileSystemConfig.getAllocationScheme() != AllocationScheme.FAT) {
          openFATButton.setVisible(false);
      }  else {
          legendBox.getChildren().removeAll(
              freeBlocksLegend
          );
      }

      List<Node> snapshot = new ArrayList<>(legendBox.getChildren());

      rebuildLegend(snapshot);

      viewModel.getLegendEntries().addListener(
          (ListChangeListener<LegendEntry>) change -> {
              while (change.next()) {
                  rebuildLegend(snapshot);
              }
          }
      );
    }

    private void rebuildLegend(List<Node> snapshot) {
        legendBox.getChildren().clear();
        legendBox.getChildren().addAll(snapshot);

        for (LegendEntry e : viewModel.getLegendEntries()) {
            addLegendEntry(e.blockRecord());
        }
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

    private void addLegendEntry(BlockRecord blockRecord) {
        String labelText = switch (blockRecord.type()){
          case DIRECTORY -> "Diretório: " + blockRecord.userString();
          case FILE -> "Arquivo: " + blockRecord.userString();
          case INODE_INDIRECT_BLOCK -> "Bloco Indireto (i-node " + blockRecord.userInt() + "):";
          default -> throw new Error("unhandled switch case");
        };

        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 15;");
        label.setPrefWidth(250);
    
        Rectangle rect = new Rectangle(16, 16);
        rect.setFill(BlockRecord.getCorrespondingColor(blockRecord));
    
        HBox row = new HBox(0);
        row.getChildren().addAll(label, rect);
    
        legendBox.getChildren().add(row);
    }

    @FXML
    private void handleOpenFAT() {
        int width = 320;
        int height = 600;
    
        var viewTuple = FluentViewLoader.fxmlView(FATView.class)
                .viewModel(
                  new FATViewModel(viewModel.getFileSystemSimulation())
                )
                .load();
    
        Stage stage = new Stage();
        stage.setScene(new Scene(viewTuple.getView(), width, height));
        stage.setTitle("File Allocation Table (FAT)");
        stage.show();
    }

}
