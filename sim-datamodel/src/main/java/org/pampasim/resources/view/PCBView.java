package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
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
    @FXML private Label currExecTimeLabel;
    @FXML private Label burstLabel;

    // Memory Management Info

    @FXML public TitledPane processMemoryInfoTitledPane;

    @FXML public Label memorySizeLabel;
    @FXML public Label maxPagesRamLabel;
    @FXML public Label pageHitsLabel;
    @FXML public Label pageFaultsLabel;
    @FXML public Label pageFaultRateLabel;
    @FXML public Label workingSetWindowLabel;

    @FXML public Label workingSetListLabel;
    @FXML public Label accessListLabel;

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
        currExecTimeLabel.textProperty().bind(viewModel.getCurrExecTime().asString());

        burstLabel.textProperty().bind(viewModel.getBurst().asString());


        // Memory info
        MemoryInfoViewModel memoryInfo = viewModel.getModuleInfoViewModel(MemoryInfoViewModel.class);

        processMemoryInfoTitledPane.visibleProperty().set(memoryInfo != null);
        processMemoryInfoTitledPane.managedProperty().set(memoryInfo != null);

        if (memoryInfo != null) {
            memorySizeLabel.textProperty().bind(memoryInfo.getProcessSize().asString());
            maxPagesRamLabel.textProperty().bind(memoryInfo.getMaxPagesRam().asString());
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
        }
    }

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



}
