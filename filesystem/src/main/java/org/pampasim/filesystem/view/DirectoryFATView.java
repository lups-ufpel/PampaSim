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
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.beans.Observable;
import javafx.stage.Stage;
import javafx.scene.Scene;

import org.pampasim.filesystem.viewmodel.DirectoryViewModel;
import org.pampasim.filesystem.viewmodel.MetadataViewModel;
import org.pampasim.filesystem.directory.DirectoryEntry;
import org.pampasim.filesystem.file.reference.FATFileReference;

public class DirectoryFATView implements FxmlView<DirectoryViewModel> {
    @InjectViewModel
    private DirectoryViewModel viewModel;
    @FXML
    private TableView<DirectoryEntry> tableView;
    @FXML
    private TableColumn<DirectoryEntry, String> fileNameColumn;
    @FXML
    private TableColumn<DirectoryEntry, String> firstBlockColumn;
    @FXML
    private TableColumn<DirectoryEntry, Void> metadataColumn = new TableColumn<>("Metadados");

    @FXML
    public void initialize() {

        fileNameColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getName())
        );

        firstBlockColumn.setCellValueFactory(cell -> {
            int firstBlock = ((FATFileReference)
                    cell.getValue().getFileReference())
                    .getFirstBlockIndex();
        
            return new ReadOnlyObjectWrapper<>(
                firstBlock == -2
                    ? "-2 (EOF)"
                    : String.valueOf(
                        viewModel.getFileSystem().absoluteAddressOf(firstBlock)
                    )
            );
        });    

        metadataColumn.setCellFactory(col -> new TableCell<>() {
        
            private final Button button = new Button("Expandir");
            {
                button.setMaxWidth(Double.MAX_VALUE);
                
                button.setOnAction(e -> {
                var viewTuple = FluentViewLoader.fxmlView(MetadataView.class)
                                                .viewModel(new MetadataViewModel(viewModel.getFileSystemSimulation(),  viewModel.getFilePath(getIndex())))
                                                .load();

                Stage stage = new Stage();
                stage.setScene(new Scene(viewTuple.getView(), 400, 150));
                stage.setTitle("Metadados");
                stage.show();

                });
            }
        
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
        
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        tableView.setItems(viewModel.getEntries());
        viewModel.getEntries().addListener((Observable obs) -> {
            tableView.refresh();
        });
    }

}
