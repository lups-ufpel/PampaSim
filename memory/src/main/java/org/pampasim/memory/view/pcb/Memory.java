package org.pampasim.memory.view.pcb;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.pampasim.memory.viewmodel.MemoryInfoViewModel;
import org.pampasim.memory.viewmodel.PageTableEntryViewModel;
import org.pampasim.memory.viewmodel.PageTableViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.function.Function;

public class Memory implements FxmlView<ProcessViewModel> {
    @InjectViewModel
    ProcessViewModel viewModel;

    @FXML public TitledPane processMemoryInfoTitledPane;

    @FXML public Label memorySizeLabel;
    @FXML public Label maxPagesRamLabel;
    @FXML public Label ioWaitingTime;
    @FXML public Label pageHitsLabel;
    @FXML public Label pageFaultsLabel;
    @FXML public Label pageFaultRateLabel;
    @FXML public Label workingSetWindowLabel;

    @FXML public Label workingSetListLabel;
    @FXML public Label accessListLabel;

    @FXML public Tab pageTableTab;

    @FXML private Label noPageTableLabel;
    @FXML private TableView<PageTableEntryViewModel> pageTableView;

    @FXML private TableColumn<PageTableEntryViewModel, Integer> pageNumberColumn;
    @FXML private TableColumn<PageTableEntryViewModel, Boolean> validColumn;
    @FXML private TableColumn<PageTableEntryViewModel, Boolean> dirtyColumn;
    @FXML private TableColumn<PageTableEntryViewModel, Boolean> referencedColumn;
    @FXML private TableColumn<PageTableEntryViewModel, Boolean> fileBackedColumn;
    @FXML private TableColumn<PageTableEntryViewModel, String> frameAddressColumn;

    public void initialize() {
        MemoryInfoViewModel memoryInfo = viewModel.getModuleInfoViewModel(MemoryInfoViewModel.class);

        processMemoryInfoTitledPane.visibleProperty().set(memoryInfo != null);
        processMemoryInfoTitledPane.managedProperty().set(memoryInfo != null);

        if (memoryInfo == null) {
            // Remove the page table tab if memoryInfo is null
            TabPane tabPane = pageTableTab.getTabPane();
            if (tabPane != null) {
                tabPane.getTabs().remove(pageTableTab);
            }
        } else {
            memorySizeLabel.textProperty().bind(memoryInfo.getProcessSize().asString());
            maxPagesRamLabel.textProperty().bind(memoryInfo.getMaxPagesRam().asString());
            ioWaitingTime.textProperty().bind(memoryInfo.getIoWaitingTime().asString());
            pageHitsLabel.textProperty().bind(memoryInfo.getPageHits().asString());
            pageFaultsLabel.textProperty().bind(memoryInfo.getPageFaults().asString());
            pageFaultRateLabel.textProperty().bind(
                    Bindings.createStringBinding(
                            () -> String.format("%.1f%%", memoryInfo.getPageFaultRate().get() * 100),
                            memoryInfo.getPageFaultRate()
                    )
            );

            workingSetWindowLabel.textProperty().bind(memoryInfo.getWorkingSetWindow().asString());

            workingSetListLabel.textProperty().bind(memoryInfo.getWorkingSet().asString().map(list ->
                    list.replaceAll("[\\[\\]]", "")
            ));

            accessListLabel.textProperty().bind(memoryInfo.getTotalAccessList().asString().map(list ->
                    list.replaceAll("[\\[\\]]", "")
            ));

            // page table

            //here, set tab to visible

            memoryInfo.getPageTableViewModel().addListener((obs, oldVal, newVal) -> {
                boolean hasTable = newVal != null;

                pageTableView.setVisible(hasTable);
                pageTableView.setManaged(hasTable);
                noPageTableLabel.setVisible(!hasTable);
                noPageTableLabel.setManaged(!hasTable);

                if (hasTable) {
                    updatePageTableUI(newVal);
                } else {
                    pageTableView.setItems(null);
                }
            });

            PageTableViewModel pageTable = memoryInfo.getPageTableViewModel().get();
            boolean hasTable = pageTable != null;

            pageTableView.setVisible(hasTable);
            pageTableView.setManaged(hasTable);
            noPageTableLabel.setVisible(!hasTable);
            noPageTableLabel.setManaged(!hasTable);

            if (hasTable) {
                updatePageTableUI(pageTable);
            }

        }
    }

    private void updatePageTableUI(PageTableViewModel pageTable) {
        pageTableView.setItems(pageTable.getEntries());

        pageNumberColumn.setCellValueFactory(cell -> cell.getValue().pageNumberProperty().asObject());
        frameAddressColumn.setCellValueFactory(cell -> Bindings.createStringBinding(
                () -> {
                    Integer address = cell.getValue().frameAddressProperty().get();
                    return address != null ? address.toString() : "Indefinido";
                },
                cell.getValue().frameAddressProperty()
        ));

        setupBooleanColumn(validColumn, PageTableEntryViewModel::validProperty);
        setupBooleanColumn(dirtyColumn, PageTableEntryViewModel::dirtyProperty);
        setupBooleanColumn(referencedColumn, PageTableEntryViewModel::referencedProperty);
        setupBooleanColumn(fileBackedColumn, PageTableEntryViewModel::fileBackedProperty);
    }


    private void setupBooleanColumn(TableColumn<PageTableEntryViewModel, Boolean> column,
                                    Function<PageTableEntryViewModel, ObservableValue<Boolean>> prop) {
        column.setCellValueFactory(cell -> prop.apply(cell.getValue()));

        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle(""); // fallback
                } else {
                    setText(value.toString());
                    String background = value ? "#c8f7c5" : "#f7c5c5"; // light green / light red
                    setStyle("-fx-background-color: " + background + ";"
                            + " -fx-border-color: -fx-table-cell-border-color;");
                }
            }
        });
    }
}
