package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.memory.events.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class VirtualMemory extends AbstractSimEntity {
    public VirtualMemory(Simulation simulation) {
        super(simulation);
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ProcessAllocate e -> handleProcessAllocate(e);
            case ProcessEnd e -> handleProcessEnd(e);
            case ProcessDispatch e -> handleProcessDispatch(e);
            case ProcessIoOperation e -> handleProcessIoOperation(e);

            case MemoryReserveProcessMemoryFinished e -> handleMemoryReserveProcessMemoryFinished(e);
            case MemoryFreeProcessMemoryFinished e -> handleMemoryFreeProcessMemoryFinished(e);
            case MemoryIoOperationFinished e -> handleMemoryIoOperationFinished(e);
            case MemoryProcessReady e -> handleMemoryProcessReady(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessAllocate(ProcessAllocate event) {
        //TODO: Stub
        // TODO: could also result in a ProcessKill event if there aren't enough pages available in the virtual memory for the process
        scheduleToNextClock(new MemoryCreatePageTableEntry(this, event.getProcess()));
        scheduleToNextClock(new ProcessKill(this, event.getProcess()));
    }

    private void handleProcessEnd(ProcessEnd event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryDeletePageTableEntry(this, event.getProcess()));
    }

    private void handleProcessDispatch(ProcessDispatch event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryTranslateVirtualAddress(this, event.getProcess()));
    }

    private void handleProcessIoOperation(ProcessIoOperation event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryIoOperation(this, event.getProcess()));
    }

    private void handleMemoryReserveProcessMemoryFinished(MemoryReserveProcessMemoryFinished event) {
        //TODO: Stub
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemoryFinished(MemoryFreeProcessMemoryFinished event) {
        //TODO: Stub
        scheduleToNextClock(new ProcessKill(this, event.getProcess()));
    }

    private void handleMemoryIoOperationFinished(MemoryIoOperationFinished event) {
        //TODO: Stub
        scheduleToNextClock(new ProcessSchedule(this, event.getProcess()));
    }

    private void handleMemoryProcessReady(MemoryProcessReady event) {
        //TODO: Stub
        //TODO: could also result in a ProcessSchedule event if there was an IO operation needed
        scheduleToNextClock(new ProcessRun(this, event.getProcess()));
        scheduleToNextClock(new ProcessSchedule(this, event.getProcess()));
    }

}
