package org.pampasim.viewModel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.pampasim.core.SimulationBase;
import org.pampasim.resources.viewmodel.ModuleInfoViewModel;
import org.pampasim.resources.viewmodel.StatisticsViewModel;
import org.pampasim.entity.Processor;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.entity.PhysicalMemory;
import org.pampasim.resources.Process;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.List;

@Getter
public class SimulationStatisticsViewModel implements StatisticsViewModel {

    // Processor
    public final SimpleIntegerProperty elapsedTicks = new SimpleIntegerProperty();
    public final SimpleIntegerProperty totalProcessesCreated = new SimpleIntegerProperty();
    public final SimpleIntegerProperty completedProcesses = new SimpleIntegerProperty();
    public final SimpleIntegerProperty elapsedCpuBusyTicks = new SimpleIntegerProperty();
    public final SimpleIntegerProperty accumulatedReadyWaitingTicks = new SimpleIntegerProperty();
    public final SimpleDoubleProperty processorUsagePercentage = new SimpleDoubleProperty();
    public final SimpleDoubleProperty averageReadyWaitingTicks = new SimpleDoubleProperty();
    public final SimpleDoubleProperty averageProcessTurnaroundTime = new SimpleDoubleProperty();
    public final SimpleDoubleProperty processThroughputPerTick = new SimpleDoubleProperty();

    private final ObservableList<StatisticsViewModel> moduleStatisticsViewModels = FXCollections.observableArrayList();

    public void updateStatistics(SimulationBase simulation, ObservableList<ProcessViewModel> processes) {
        elapsedTicks.set(simulation.getRealClock().getTick());
        totalProcessesCreated.set(processes.size());

        List<ProcessViewModel> terminated = processes.stream()
                .filter(p -> p.getState() == Process.State.TERMINATED)
                .toList();

        completedProcesses.set(terminated.size());
        elapsedCpuBusyTicks.set(simulation.getEntity(Processor.class).getBusyTicks());

        int totalReadyWaitingTicks = processes.stream()
                .mapToInt(p -> p.getReadyWaitingTime().get())
                .sum();
        accumulatedReadyWaitingTicks.set(totalReadyWaitingTicks);
        averageReadyWaitingTicks.set(
                processes.isEmpty() ? 0.0 :
                        (double) totalReadyWaitingTicks / processes.size()
        );

        processorUsagePercentage.set(
                elapsedTicks.get() == 0 ? 0.0 :
                        (double) elapsedCpuBusyTicks.get() / elapsedTicks.get()
        );

        // Average Process Turnaround Time
        int totalTurnaround = terminated.stream()
                .mapToInt(p -> p.getEndTime().get() - p.getArrivalTick().get())
                .sum();
        averageProcessTurnaroundTime.set(
                terminated.isEmpty() ? 0.0 :
                        (double) totalTurnaround / terminated.size()
        );

        // Throughput
        processThroughputPerTick.set(
                elapsedTicks.get() == 0 ? 0.0 :
                        (double) terminated.size() / elapsedTicks.get()
        );
    }

    public void addModuleStatisticsViewModel(StatisticsViewModel moduleViewModel) {
        this.moduleStatisticsViewModels.add(moduleViewModel);
    }

    public <T extends StatisticsViewModel> T getModuleStatisticsViewModel(Class<T> clazz) {
        return moduleStatisticsViewModels.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}
