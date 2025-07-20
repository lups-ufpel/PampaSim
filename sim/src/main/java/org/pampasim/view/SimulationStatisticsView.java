package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.pampasim.viewModel.SimulationStatisticsViewModel;


public class SimulationStatisticsView implements FxmlView<SimulationStatisticsViewModel> {

    @InjectViewModel
    private SimulationStatisticsViewModel viewModel;

    @FXML private Label elapsedTicksLabel;
    @FXML private Label totalProcessesLabel;
    @FXML private Label completedProcessesLabel;
    @FXML private Label cpuBusyTicksLabel;
    @FXML private Label readyWaitingTicksLabel;
    @FXML private Label processorUsageLabel;
    @FXML private Label avgReadyWaitLabel;
    @FXML private Label avgTurnaroundLabel;
    @FXML private Label throughputLabel;



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
    }
}
