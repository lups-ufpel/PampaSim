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
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.GridPane;

import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.viewmodel.InodeViewModel;

public class InodeView implements FxmlView<InodeViewModel> {

    @InjectViewModel
    private InodeViewModel viewModel;

    @FXML private TableView<InodeTableRow> table;
    @FXML private TableColumn<InodeTableRow, String> fieldColumn;
    @FXML private TableColumn<InodeTableRow, Number> valueColumn;

    @FXML
    public void initialize() {

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        fieldColumn.setCellValueFactory(cell ->
            cell.getValue().fieldProperty()
        );

        valueColumn.setCellValueFactory(cell ->
            cell.getValue().valueProperty()
        );

        buildTable();
    }

    private void buildTable() {
        table.getItems().clear();

        // direct addresses
        for (int i = 0; i < Inode.ADDRESSES_NUMBER; i++) {
            table.getItems().add(
                new InodeTableRow(
                    "Endereço direto " + i,
                    viewModel.directAddressProperty(i)
                )
            );
        }

        // singly indirect pointer
        table.getItems().add(
            new InodeTableRow(
                "Ponteiro singularmente indireto",
                viewModel.singlyIndirectPointerProperty()
            )
        );
    }
}
