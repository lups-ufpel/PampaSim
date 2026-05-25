package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
/*
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.PageTableEntryViewModel;
import org.pampasim.resources.viewmodel.PageTableViewModel;
 */
import org.pampasim.resources.viewmodel.ProcessViewModel;

public class PCBView implements FxmlView<ProcessViewModel> {

    @InjectViewModel
    private ProcessViewModel viewModel;

    // Process Management Info

    @FXML private Label pidLabel;
    @FXML public Label colorLabel;
    @FXML private Label stateLabel;
    @FXML private Label arrivalTickLabel;
    @FXML private Label priorityLabel;
    @FXML private Label readyWaitingTime;
    @FXML private Label currExecTimeLabel;
    @FXML private Label burstLabel;

    // Memory Management Info

    /*
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
    */


    public void initialize() {
        // Process info
        pidLabel.textProperty().bind(viewModel.getPid().asString());
        Color color = viewModel.getColorProperty().get();
        if (color != null) {
            String hex = toHex(color);
            colorLabel.setText(hex);
            colorLabel.setStyle("-fx-background-color: " + hex + "; -fx-text-fill: " + getTextColorForBackground(color) + ";");
        }
        stateLabel.textProperty().bind(viewModel.stateProperty().asString());
        arrivalTickLabel.textProperty().bind(viewModel.getArrivalTick().asString());
        priorityLabel.textProperty().bind(viewModel.getPriority().asString());
        readyWaitingTime.textProperty().bind(viewModel.getReadyWaitingTime().asString());
        currExecTimeLabel.textProperty().bind(viewModel.getCurrExecTime().asString());
        burstLabel.textProperty().bind(viewModel.getBurst().asString());


        /*
        // Memory info
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
        */
    }

    /*
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
     */

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private String getTextColorForBackground(Color color) {
        double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        return luminance < 0.5 ? "white" : "black";
    }

    /*
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
     */

}
