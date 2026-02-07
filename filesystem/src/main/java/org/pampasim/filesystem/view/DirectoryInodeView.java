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
import org.pampasim.filesystem.file.reference.InodeFileReference;

public class DirectoryInodeView implements FxmlView<DirectoryViewModel> {
    @InjectViewModel
    private DirectoryViewModel viewModel;
    @FXML
    private TableView<DirectoryEntry> tableView;
    @FXML
    private TableColumn<DirectoryEntry, String> fileNameColumn;
    @FXML
    private TableColumn<DirectoryEntry, Integer> inodeIndexColumn;

    @FXML
    public void initialize() {

        fileNameColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getName())
        );

        inodeIndexColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(((InodeFileReference)cell.getValue().getFileReference()).getIndex())
        );

        //changes dont propagate, whatever
        tableView.setItems(
            FXCollections.observableArrayList(viewModel.getEntries())
        );
    }

}
