package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.pampasim.filesystem.viewmodel.FATViewModel;
import javafx.scene.control.TableCell;
import javafx.beans.Observable;

public class FATView implements FxmlView<FATViewModel> {

    @FXML
    private TableView<FATEntry> tableView;

    @FXML
    private TableColumn<FATEntry, Number> indexColumn;

    @FXML
    private TableColumn<FATEntry, Number> valueColumn;

    @InjectViewModel
    private FATViewModel viewModel;

    public void initialize() {

        indexColumn.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createIntegerBinding(() -> {
                    int value = cell.getValue().getIndex();
                    return viewModel.getFileSystemSimulation().getFileSystem().absoluteAddressOf(value);
                }, cell.getValue().indexProperty())
        );

        valueColumn.setCellValueFactory(cell ->
                javafx.beans.binding.Bindings.createIntegerBinding(() -> {
                    int value = cell.getValue().getValue();
        
                    if (value == -1 || value == -2) {
                        return value;
                    }
        
                    return viewModel.getFileSystemSimulation().getFileSystem().absoluteAddressOf(value);
                }, cell.getValue().valueProperty())
        );
        
        valueColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
        
                if (empty || item == null) {
                    setText(null);
                } else {
                    int value = item.intValue();
        
                    if (value == -1) {
                        setText("-1 (não usado)");
                    } else if (value == -2) {
                        setText("-2 (EOF)");
                    } else {
                        setText(String.valueOf(value));
                    }
                }
            }
        });

        tableView.setItems(viewModel.getFatEntries());
        viewModel.getFatEntries().addListener((Observable obs) -> {
            tableView.refresh();
        });

    }
}
