package org.pampasim.resources.viewmodel;

import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.memory.ProcessMemoryInfo.IoOperationType;

@Getter
@Setter
public class MemoryInfoViewModel extends ModuleInfoViewModel {

    // Tempo total que a operação de I/O atual deveria levar
    private final IntegerProperty totalIoOperationLength = new SimpleIntegerProperty(0);

    // Tempo restante da operação de I/O atual
    private final IntegerProperty remainingIoOperationTime = new SimpleIntegerProperty(0);

    // Tipo da operação de I/O atual
    private final ObjectProperty<IoOperationType> currentIoOperationType = new SimpleObjectProperty<>(null);

    // Progresso da operação de I/O atual
    private final ObjectProperty<Double> ioOperationProgress = new SimpleObjectProperty<>(0.0);

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
        IoOperationType currentType = memoryInfo.getCurrentIoOperation();
        int timeRemaining = memoryInfo.getCurrentIoOperationTimeRemaining();
        Integer scheduledLength = null;

        currentIoOperationType.set(currentType);
        remainingIoOperationTime.set(timeRemaining);

        int operationLength = 0;

        if (currentType == ProcessMemoryInfo.IoOperationType.PAGE_FAULT) {
            // For PAGE_FAULT, use swappingOperationsLength
            operationLength =  memoryInfo.getSwappingOperationsLength();
        }
        else if (currentType == ProcessMemoryInfo.IoOperationType.DISK_ACCESS) {
            // For DISK_ACCESS, get from scheduled IO operations
            scheduledLength = memoryInfo.getScheduledIoOperation(memoryInfo.getProcess().getCurrExecTime());
            operationLength = scheduledLength != null ? scheduledLength : 0;
        }

        totalIoOperationLength.set(operationLength);
        ioOperationProgress.set((operationLength - remainingIoOperationTime.doubleValue()) / operationLength);
    }
}
