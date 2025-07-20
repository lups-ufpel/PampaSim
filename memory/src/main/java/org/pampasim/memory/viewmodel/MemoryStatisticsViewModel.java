package org.pampasim.memory.viewmodel;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import org.pampasim.core.SimulationBase;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.entity.PhysicalMemory;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;
import org.pampasim.resources.viewmodel.StatisticsViewModel;

public class MemoryStatisticsViewModel implements StatisticsViewModel {

    // Memory
    public final SimpleIntegerProperty framesInRam = new SimpleIntegerProperty();
    public final SimpleIntegerProperty framesInSwap = new SimpleIntegerProperty();
    public final SimpleIntegerProperty pageSize = new SimpleIntegerProperty();
    public final SimpleIntegerProperty totalPageFaults = new SimpleIntegerProperty();
    public final SimpleIntegerProperty totalPageHits = new SimpleIntegerProperty();
    public final SimpleIntegerProperty usedRamFrames = new SimpleIntegerProperty();
    public final SimpleIntegerProperty usedSwapFrames = new SimpleIntegerProperty();
    public final SimpleIntegerProperty accumulatedIoWaitingTicks = new SimpleIntegerProperty();
    public final SimpleDoubleProperty freeRamPercentage = new SimpleDoubleProperty();
    public final SimpleDoubleProperty freeSwapPercentage = new SimpleDoubleProperty();
    public final SimpleDoubleProperty totalPageFaultPercentage = new SimpleDoubleProperty();
    public final SimpleDoubleProperty averageIoWaitingTicks = new SimpleDoubleProperty();

    public void updateStatistics(SimulationBase simulation, ObservableList<ProcessViewModel> allProcesses) {
        MemoryManagement memoryManagement = (MemoryManagement) simulation;
        PhysicalMemory physicalMemory = memoryManagement.getEntity(PhysicalMemory.class);

        framesInRam.set(physicalMemory.getMainMemory().size());
        framesInSwap.set(physicalMemory.getSwapFile().size());

        // TODO: I want to eventually decouple from the static MemoryConfig class but this will do for now
        pageSize.set(MemoryConfig.getPageSize());

        usedRamFrames.set(physicalMemory.getMainMemory().getTotalAllocatedFrames());
        usedSwapFrames.set(physicalMemory.getSwapFile().getTotalAllocatedFrames());

        totalPageFaults.set(physicalMemory.getPageFaults());
        totalPageHits.set(physicalMemory.getPageHits());

        int totalIoWaitingTicks = allProcesses.stream()
                .map(p -> p.getModuleInfoViewModel(MemoryInfoViewModel.class))
                .mapToInt(vm -> vm.getIoWaitingTime().get())
                .sum();

        accumulatedIoWaitingTicks.set(totalIoWaitingTicks);
        averageIoWaitingTicks.set(
                allProcesses.isEmpty() ? 0.0 :
                        (double) totalIoWaitingTicks / allProcesses.size()
        );
        freeRamPercentage.set(
                framesInRam.get() == 0 ? 0.0 :
                        (1 - ((double) usedRamFrames.get() / framesInRam.get()))
        );

        freeSwapPercentage.set(
                framesInSwap.get() == 0 ? 0.0 :
                        (1 - ((double) usedSwapFrames.get() / framesInSwap.get()))
        );
        int totalPageAccesses = totalPageFaults.get() + totalPageHits.get();
        totalPageFaultPercentage.set(
                totalPageAccesses == 0 ? 0.0 :
                        (double) totalPageFaults.get() / totalPageAccesses
        );
    }
}
