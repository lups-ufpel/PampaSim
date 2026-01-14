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

import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.viewmodel.MbrViewModel;

public class MbrView implements FxmlView<MbrViewModel> {

    @FXML
    private TableView<Partition> tableView;
    @FXML
    private TableColumn<Partition, Integer> firstBlockColumn;
    @FXML
    private TableColumn<Partition, Integer> lastBlockColumn;
    @FXML
    private TableColumn<Partition, Boolean> activeColumn;
    @FXML
    private TableColumn<Partition, Number> indexColumn = new TableColumn<>("#");

    @InjectViewModel
    private MbrViewModel viewModel;

    @FXML
    public void initialize() {
        
        indexColumn.setCellValueFactory(cell ->
            new ReadOnlyObjectWrapper<>(
                cell.getTableView().getItems().indexOf(cell.getValue())
            )
        );

        firstBlockColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getFirstBlockIndex())
        );
        
        lastBlockColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getLastBlockIndex())
        );
        
        activeColumn.setCellValueFactory(
            cell -> new ReadOnlyObjectWrapper<>(cell.getValue().isActive())
        );

        //changes dont propagate, whatever
        tableView.setItems(
            FXCollections.observableArrayList(viewModel.getPartitions())
        );
    }
}
