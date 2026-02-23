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
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;

import org.pampasim.filesystem.FileSystemSimulation;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.viewmodel.*;

public class BlockView implements FxmlView<BlockViewModel> {
    @InjectViewModel
    private BlockViewModel viewModel;

    @FXML
    public VBox blockVBox;
    @FXML
    public Label number;
    @FXML
    public Label blockLabel;

    // used to create a viewModel if not created before
    private final Map<Class<?>, Object> viewModelCache = new HashMap<>();
    
    @SuppressWarnings("unchecked")
    private <T> T cached(Class<T> type, Supplier<T> factory) {
        return (T) viewModelCache.computeIfAbsent(type, k -> factory.get());
    }
    //

    public void initialize() {
        number.setText(Integer.toString(viewModel.getNumber()));

        //blockLabel.textProperty().bind(viewModel.getCircleLabel());
        blockLabel.textProperty().set("");

        if(viewModel.getType() != BlockType.EMPTY){
          blockVBox.setCursor(Cursor.HAND);
        }
        blockVBox.setOnMouseClicked(e -> {
          openWindow(viewModel.getBlockRecord(), viewModel.getFileSystemSimulation());
        });
  
        blockVBox.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> new Background(
                    new BackgroundFill(
                        BlockRecord.getCorrespondingColor(viewModel.getBlockRecord()),
                        new CornerRadii(5),
                        Insets.EMPTY
                    )
                ),
                viewModel.blockRecordProperty()
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

    public void openWindow(BlockRecord blockRecord, FileSystemSimulation fsSim) {
        int width = 600;
        int height = 600;
    
        var viewTuple = switch (blockRecord.type()) {
            case EMPTY -> null;
    
            case MBR -> FluentViewLoader.fxmlView(MbrView.class)
                .viewModel(
                    cached(
                        MbrViewModel.class,
                        () -> new MbrViewModel(fsSim.getDisk().getPartitions())
                    )
                )
                .load();
    
            case INITIALIZATION -> {
                width = 600;
                height = 200;
            yield FluentViewLoader.fxmlView(InitializationView.class)
                .viewModel(
                    cached(InitializationViewModel.class, InitializationViewModel::new)
                )
                .load();

            }    
            case SUPERBLOCK -> {
                width = 600;
                height = 200;
                yield FluentViewLoader.fxmlView(SuperblockView.class)
                    .viewModel(
                        cached(SuperblockViewModel.class, SuperblockViewModel::new)
                    )
                    .load();
            }
    
            case FREE_BLOCKS_BITMAP -> FluentViewLoader.fxmlView(FreeBlocksView.class)
                .viewModel(
                    cached(
                        FreeBlocksViewModel.class,
                        () -> new FreeBlocksViewModel(
                            fsSim,
                            fsSim.getFileSystem().getFreeBlocksBitMap()
                        )
                    )
                )
                .load();
    
            case FREE_INODES_BITMAP -> FluentViewLoader.fxmlView(FreeInodesView.class)
                .viewModel(
                    cached(
                        FreeInodesViewModel.class,
                        () -> new FreeInodesViewModel(
                            fsSim, fsSim.getFileSystem().getFreeInodesBitMap()
                        )
                    )
                )
                .load();
    
            case FILE -> FluentViewLoader.fxmlView(FileView.class)
                .viewModel(
                    cached(FileViewModel.class, FileViewModel::new)
                )
                .load();
    
            case DIRECTORY -> {
                var fs = fsSim.getFileSystem();
                String path = blockRecord.userString();
    
                DirectoryViewModel dirVm =
                    cached(
                        DirectoryViewModel.class,
                        () -> new DirectoryViewModel(fsSim, path)
                    );
    
                yield switch (fs.getAllocationScheme()) {
                    case INODES -> {
                        width = 280;
                        height = 550;
                        yield FluentViewLoader.fxmlView(DirectoryInodeView.class)
                            .viewModel(dirVm)
                            .load();
                    }
                    case FAT -> {
                        width = 420;
                        height = 400;
                        yield FluentViewLoader.fxmlView(DirectoryFATView.class)
                            .viewModel(dirVm)
                            .load();
                    }
                    case CONTIGUOUS -> {
                        width = 600;
                        height = 400;
                        yield FluentViewLoader.fxmlView(DirectoryContiguousView.class)
                            .viewModel(dirVm)
                            .load();
                    }
                };
            }
    
            case INODE_TABLE -> FluentViewLoader.fxmlView(InodeTableView.class)
                .viewModel(
                    cached(
                        InodeTableViewModel.class,
                        () -> new InodeTableViewModel(fsSim)
                    )
                )
                .load();

            case INODE_INDIRECT_BLOCK -> FluentViewLoader.fxmlView(InodeIndirectBlockView.class)
                .viewModel(
                    cached(
                        InodeViewModel.class,
                        () -> new InodeViewModel(fsSim, blockRecord.userInt()) // userInt == inodeIndex
                    )
                )
                .load();
    
            default -> throw new Error("unhandled");
        };
    
        if (viewTuple != null) {
            Stage stage = new Stage();
            stage.setScene(new Scene(viewTuple.getView(), width, height));
            stage.setTitle(getCorrespondingWindowTitle(blockRecord));
            stage.show();
        }
    }

    public String getCorrespondingWindowTitle(BlockRecord blockRecord){
        return switch (blockRecord.type()) {
            case EMPTY -> null;

            case MBR -> "Master Boot Record";
        
            case INITIALIZATION -> "Bloco de Inicialização";
        
            case SUPERBLOCK -> "Superbloco";
        
            case FREE_BLOCKS_BITMAP -> "Mapa de Blocos Livres";
        
            case FREE_INODES_BITMAP -> "Mapa de I-nodes Livres";

            case FILE -> "Arquivo " + "\"" + blockRecord.userString() + "\"";
        
            case DIRECTORY -> "Diretório " + "\"" + blockRecord.userString() + "\"";

            case INODE_TABLE -> "Tabela de I-nodes";
            
            case INODE_INDIRECT_BLOCK -> "Bloco de Endereços Indiretos do I-node " + blockRecord.userInt();

            default ->
              throw new Error("unhandled");
    };
  }



}
