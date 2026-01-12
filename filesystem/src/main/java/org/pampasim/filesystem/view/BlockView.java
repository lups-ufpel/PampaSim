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

import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.viewmodel.BlockViewModel;
import org.pampasim.filesystem.viewmodel.MbrViewModel;
import org.pampasim.filesystem.viewmodel.InitializationViewModel;
import org.pampasim.filesystem.viewmodel.SuperblockViewModel;
import org.pampasim.filesystem.viewmodel.FreeBlocksViewModel;
import org.pampasim.filesystem.viewmodel.FreeInodesViewModel;
import org.pampasim.filesystem.viewmodel.DirectoryViewModel;
import org.pampasim.filesystem.viewmodel.FileViewModel;
import org.pampasim.filesystem.viewmodel.InodeTableViewModel;

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

        if(viewModel.getType() != BlockType.EMPTY){
          blockVBox.setCursor(Cursor.HAND);
        }
        blockVBox.setOnMouseClicked(e -> {
          System.out.println("clicked on block " + number);
          openWindow(viewModel.getBlockRecord());
        });
  
        blockVBox.backgroundProperty().bind(
            Bindings.createObjectBinding(
                () -> new Background(
                    new BackgroundFill(
                        BlockType.getCorrespondingColor(viewModel.getType()),
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

  // probably dont create a new viewmodel every single time
    public void openWindow(BlockRecord blockRecord){
        var viewTuple = switch (blockRecord.type()) {
            case EMPTY -> null;

            case MBR -> FluentViewLoader.fxmlView(MbrView.class)
                    .viewModel(new MbrViewModel())
                    .load();
        
            case INITIALIZATION -> FluentViewLoader.fxmlView(InitializationView.class)
                    .viewModel(new InitializationViewModel())
                    .load();
        
            case SUPERBLOCK -> FluentViewLoader.fxmlView(SuperblockView.class)
                    .viewModel(new SuperblockViewModel())
                    .load();
        
            case FREE_BLOCKS_BITMAP -> FluentViewLoader.fxmlView(FreeBlocksView.class)
                    .viewModel(new FreeBlocksViewModel())
                    .load();
        
            case FREE_INODES_BITMAP -> FluentViewLoader.fxmlView(FreeInodesView.class)
                    .viewModel(new FreeInodesViewModel())
                    .load();

            case FILE -> FluentViewLoader.fxmlView(FileView.class)
                    .viewModel(new FileViewModel())
                    .load();
        
            case DIRECTORY -> FluentViewLoader.fxmlView(DirectoryView.class)
                    .viewModel(new DirectoryViewModel())
                    .load();

            case INODE_TABLE -> FluentViewLoader.fxmlView(InodeTableView.class)
                    .viewModel(new InodeTableViewModel())
                    .load();

            default ->
              throw new Error("unhandled");
        
      /*
            case INODE -> FluentViewLoader.fxmlView(InodeView.class)
                    .viewModel(new InodeViewModel())
                    .load();
      */
        
        };       

        if(viewTuple != null){
          Stage stage = new Stage();
          stage.setScene(new Scene(viewTuple.getView(), 600, 600));
          stage.setTitle(getCorrespondingWindowTitle(blockRecord));
          stage.show();
        }
        /*
        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        Optional<ButtonType> result = dialog.showAndWait();
        */
    }

    public String getCorrespondingWindowTitle(BlockRecord blockRecord){
        return switch (blockRecord.type()) {
            case EMPTY -> null;

            case MBR -> "Master Boot Record";
        
            case INITIALIZATION -> "Bloco de Inicialização";
        
            case SUPERBLOCK -> "Superbloco";
        
            case FREE_BLOCKS_BITMAP -> "Mapa de Blocos Livres";
        
            case FREE_INODES_BITMAP -> "Mapa de I-nodes Livres";

            case FILE -> "Arquivo" + "\"" + blockRecord.userString() + "\"";
        
            case DIRECTORY -> "Diretório" + "\"" + blockRecord.userString() + "\"";

            case INODE_TABLE -> "Tabela de I-nodes";

            default ->
              throw new Error("unhandled");
    };
  }



}
