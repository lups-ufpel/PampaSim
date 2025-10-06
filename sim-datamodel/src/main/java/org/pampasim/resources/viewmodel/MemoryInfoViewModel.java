package org.pampasim.resources.viewmodel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.memory.ProcessMemoryInfo.IoOperationType;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessPageTable;

@Getter
@Setter
public class MemoryInfoViewModel extends ModuleInfoViewModel {

    private final IntegerProperty processSize = new SimpleIntegerProperty();
    private final IntegerProperty maxPagesRam = new SimpleIntegerProperty();
    private final IntegerProperty ioWaitingTime = new SimpleIntegerProperty();
    private final IntegerProperty pageHits = new SimpleIntegerProperty();
    private final IntegerProperty pageFaults = new SimpleIntegerProperty();
    private final DoubleProperty pageFaultRate = new SimpleDoubleProperty();
    private final IntegerProperty workingSetWindow = new SimpleIntegerProperty();

    private final ListProperty<Integer> workingSet = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Integer> accessList = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Integer> totalAccessList = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final IntegerProperty totalIoOperationLength = new SimpleIntegerProperty(0);
    private final IntegerProperty remainingIoOperationTime = new SimpleIntegerProperty(0);
    private final ObjectProperty<IoOperationType> currentIoOperationType = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Double> ioOperationProgress = new SimpleObjectProperty<>(0.0);

    private final ObjectProperty<PageTableViewModel> pageTableViewModel = new SimpleObjectProperty<>();


    public MemoryInfoViewModel(ProcessMemoryInfo.CreationData creationData, int workingSetWindow, int maxPagesRam) {
        if (creationData == null) return;

        this.workingSetWindow.set(workingSetWindow);
        this.maxPagesRam.set(maxPagesRam);
        processSize.set(creationData.getSize());
        totalAccessList.setAll(creationData.getAddressAccessList());
        workingSet.setAll(FXCollections.observableArrayList());
    }


    public IntegerProperty totalIoOperationLengthProperty() {
        return totalIoOperationLength;
    }

    public IntegerProperty remainingIoOperationTimeProperty() {
        return remainingIoOperationTime;
    }

    public ObjectProperty<IoOperationType> currentIoOperationTypeProperty() {
        return currentIoOperationType;
    }

    public void updateFrom(ProcessMemoryInfo memoryInfo) {
        processSize.set(memoryInfo.getSize());
        maxPagesRam.set(memoryInfo.getMaxFrames());
        ioWaitingTime.set(memoryInfo.getIoWaitingTime());
        pageHits.set(memoryInfo.getPageHits());
        pageFaults.set(memoryInfo.getPageFaults());

        pageFaultRate.set(memoryInfo.getPageFaultRate());

        workingSetWindow.set(memoryInfo.getMemoryConfigData().getWorkingSetWindow());

        workingSet.setAll(
                memoryInfo.getWorkingSet().stream()
                        .map(PageTableEntry::getPageNumber)
                        .toList()
        );

        accessList.setAll(memoryInfo.getCurrentAccess());

        // IO operation info
        IoOperationType currentType = memoryInfo.getCurrentIoOperation();
        int timeRemaining = memoryInfo.getCurrentIoOperationTimeRemaining();
        Integer scheduledLength = null;

        currentIoOperationType.set(currentType);
        remainingIoOperationTime.set(timeRemaining);

        int operationLength = 0;
        if (currentType == IoOperationType.PAGE_FAULT) {
            operationLength = memoryInfo.getMemoryConfigData().getSwappingOperationsLength();
        } else if (currentType == IoOperationType.DISK_ACCESS) {
            scheduledLength = memoryInfo.getScheduledIoOperation(memoryInfo.getProcess().getCurrExecTime());
            operationLength = scheduledLength != null ? scheduledLength : 0;
        }

        totalIoOperationLength.set(operationLength);
        ioOperationProgress.set(operationLength > 0
                ? (operationLength - remainingIoOperationTime.get()) / (double) operationLength
                : 0.0);

        setPageTable(memoryInfo.getPageTable());


    }

    public void setPageTable(ProcessPageTable pageTable) {
        if (pageTable != null) {
            this.pageTableViewModel.set(new PageTableViewModel(pageTable));
        } else {
            this.pageTableViewModel.set(null);
        }
    }

}
