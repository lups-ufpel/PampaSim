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
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;

import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.viewmodel.InodeViewModel;
import org.pampasim.filesystem.viewmodel.MetadataViewModel;

public class InodeView implements FxmlView<InodeViewModel> {

    @InjectViewModel
    private InodeViewModel viewModel;

    @FXML private TableView<InodeTableRow> table;
    @FXML private TableColumn<InodeTableRow, String> labelColumn;
    @FXML private TableColumn<InodeTableRow, Number> valueColumn;

    @FXML
    public void initialize() {

        setupColumns();
        buildTable();
    }

    private void setupColumns(){

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        labelColumn.setCellValueFactory(cell ->
            cell.getValue().labelProperty()
        );

        valueColumn.setCellValueFactory(cell -> {
            InodeTableRow row = cell.getValue();
            return row.isMetadataRow()
                ? null
                : row.valueProperty();
        });

        valueColumn.setCellFactory(col -> new TableCell<InodeTableRow, Number>() {
        
            private final Button button = new Button("Expandir");
        
            {
                button.setMaxWidth(Double.MAX_VALUE);
                button.setOnAction(e -> {
                    var viewTuple =
                        FluentViewLoader.fxmlView(MetadataView.class)
                            .viewModel(
                                new MetadataViewModel(
                                    viewModel.getFileSystemSimulation(),
                                    viewModel.getIndex()
                                )
                            )
                            .load();
        
                    Stage stage = new Stage();
                    stage.setScene(new Scene(viewTuple.getView(), 400, 150));
                    stage.setTitle("Metadados");
                    stage.show();
                });
            }
        
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
        
                setText(null);
                setGraphic(null);
        
                if (empty) {
                    return;
                }
        
                InodeTableRow row = getTableRow().getItem();
                if (row == null) {
                    return;
                }
                if (row.isMetadataRow()) {
                    setGraphic(button);
                } else {
                    String text = (item == null) ? "" : item.toString();
                    text = (text.equals("0")) ? text + " (Vazio)" : text;
                    setText(text);
                }
            }
        });

    }

    private void buildTable() {
        table.getItems().clear();

        // metadata row
        table.getItems().add(
            new InodeTableRow("Metadados")
        );

        // direct addresses
        for (int i = 0; i < Inode.ADDRESSES_NUMBER; i++) {
            table.getItems().add(
                new InodeTableRow(
                    "Endereço Direto " + i,
                    viewModel.directAddressDisplayProperty(i)
                )
            );
        }

        // singly indirect pointer
        table.getItems().add(
            new InodeTableRow(
                "Ponteiro Singularmente Indireto",
                viewModel.singlyIndirectPointerProperty()
            )
        );
    }
}
