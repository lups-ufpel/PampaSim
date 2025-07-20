package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import org.pampasim.memory.viewmodel.MemoryStatisticsViewModel;
import org.pampasim.viewModel.SimulationStatisticsViewModel;


public class SimulationStatisticsView implements FxmlView<SimulationStatisticsViewModel> {



    @InjectViewModel
    private SimulationStatisticsViewModel viewModel;

    //Process Management
    @FXML private Label elapsedTicksLabel;
    @FXML private Label totalProcessesLabel;
    @FXML private Label completedProcessesLabel;
    @FXML private Label cpuBusyTicksLabel;
    @FXML private Label readyWaitingTicksLabel;
    @FXML private Label processorUsageLabel;
    @FXML private Label avgReadyWaitLabel;
    @FXML private Label avgTurnaroundLabel;
    @FXML private Label throughputLabel;

    //Memory Management
    @FXML private TitledPane memoryTitledPane;

    @FXML private Label framesInRamLabel;
    @FXML private Label framesInSwapLabel;
    @FXML private Label pageSizeLabel;
    @FXML private Label usedRamFramesLabel;
    @FXML private Label freeRamPercentageLabel;
    @FXML private Label usedSwapFramesLabel;
    @FXML private Label freeSwapPercentageLabel;
    @FXML private Label pageFaultsLabel;
    @FXML private Label pageHitsLabel;
    @FXML private Label pageFaultRateLabel;
    @FXML private Label accumulatedIoWaitingTicksLabel;
    @FXML private Label averageIoWaitingTicksLabel;




    public void initialize() {
        elapsedTicksLabel.textProperty().bind(viewModel.getElapsedTicks().asString());
        totalProcessesLabel.textProperty().bind(viewModel.getTotalProcessesCreated().asString());
        completedProcessesLabel.textProperty().bind(viewModel.getCompletedProcesses().asString());
        cpuBusyTicksLabel.textProperty().bind(viewModel.getElapsedCpuBusyTicks().asString());
        readyWaitingTicksLabel.textProperty().bind(viewModel.getAccumulatedReadyWaitingTicks().asString());

        processorUsageLabel.textProperty().bind(viewModel.getProcessorUsagePercentage().multiply(100).asString("%.2f%%"));
        avgReadyWaitLabel.textProperty().bind(viewModel.getAverageReadyWaitingTicks().asString("%.2f"));
        avgTurnaroundLabel.textProperty().bind(viewModel.getAverageProcessTurnaroundTime().asString("%.2f"));
        throughputLabel.textProperty().bind(viewModel.getProcessThroughputPerTick().asString("%.4f"));

        MemoryStatisticsViewModel memStats = viewModel.getModuleStatisticsViewModel(MemoryStatisticsViewModel.class);
        if (memStats != null) {
            memoryTitledPane.setVisible(true);
            memoryTitledPane.setManaged(true);

            framesInRamLabel.textProperty().bind(memStats.framesInRam.asString());
            framesInSwapLabel.textProperty().bind(memStats.framesInSwap.asString());
            pageSizeLabel.textProperty().bind(memStats.pageSize.asString());
            usedRamFramesLabel.textProperty().bind(memStats.usedRamFrames.asString());
            freeRamPercentageLabel.textProperty().bind(memStats.freeRamPercentage.multiply(100).asString("%.2f%%"));
            usedSwapFramesLabel.textProperty().bind(memStats.usedSwapFrames.asString());
            freeSwapPercentageLabel.textProperty().bind(memStats.freeSwapPercentage.multiply(100).asString("%.2f%%"));
            pageFaultsLabel.textProperty().bind(memStats.totalPageFaults.asString());
            pageHitsLabel.textProperty().bind(memStats.totalPageHits.asString());
            pageFaultRateLabel.textProperty().bind(memStats.totalPageFaultPercentage.multiply(100).asString("%.2f%%"));
            accumulatedIoWaitingTicksLabel.textProperty().bind(memStats.accumulatedIoWaitingTicks.asString());
            averageIoWaitingTicksLabel.textProperty().bind(memStats.averageIoWaitingTicks.asString("%.2f"));
        };
    }
}
