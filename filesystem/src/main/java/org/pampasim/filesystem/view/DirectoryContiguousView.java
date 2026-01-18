package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
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

import org.pampasim.filesystem.viewmodel.DirectoryViewModel;
import org.pampasim.filesystem.directory.DirectoryEntry;
import org.pampasim.filesystem.mapping.ContiguousMapping;

public class DirectoryContiguousView implements FxmlView<DirectoryViewModel> {
    @InjectViewModel
    private DirectoryViewModel viewModel;
    @FXML
    private TableView<DirectoryEntry> tableView;
    @FXML
    private TableColumn<DirectoryEntry, String> fileNameColumn;
    @FXML
    private TableColumn<DirectoryEntry, Integer> firstBlockColumn;
    @FXML
    private TableColumn<DirectoryEntry, Integer> finalSizeColumn;
    @FXML
    private TableColumn<DirectoryEntry, Void> metadataColumn = new TableColumn<>("Metadados");

    @FXML
    public void initialize() {

        fileNameColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getName())
        );

        firstBlockColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(((ContiguousMapping) cell.getValue().getFileMapping()).getFirstBlockIndex())
        );

        finalSizeColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(((ContiguousMapping) cell.getValue().getFileMapping()).getFinalSizeBlocks())
        );

        metadataColumn.setCellFactory(col -> new TableCell<>() {
        
            private final Button button = new Button("Open");
        
            {
                button.setOnAction(e -> {
                    DirectoryEntry rowItem = getTableView()
                            .getItems()
                            .get(getIndex());
        
                    // do something with rowItem
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

        //changes dont propagate, whatever
        tableView.setItems(
            FXCollections.observableArrayList(viewModel.getEntries())
        );
    }

}
