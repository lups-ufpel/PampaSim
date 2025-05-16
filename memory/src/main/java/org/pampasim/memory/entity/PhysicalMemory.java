package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.memory.events.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class PhysicalMemory extends AbstractSimEntity {
    public PhysicalMemory(Simulation simulation) {
        super(simulation);
    }

    public void processEvent(Event event) {
        switch (event) {
            case MemoryPageHit e -> handleMemoryPageHit(e);
            case MemoryPageFault e -> handleMemoryPageFault(e);

            case MemoryReserveProcessMemory e -> handleMemoryReserveProcessMemory(e);

            case MemoryFreeProcessMemory e -> handleMemoryFreeProcessMemory(e);

            case MemoryIoOperation e -> handleMemoryIoOperation(e);
            case MemoryTimeAdvance e -> handleMemoryTimeAdvance(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryTimeAdvance(MemoryTimeAdvance event) {
        //TODO: can result in a MemoryTimeAdvance or a MemoryIoOperationFinished
        //TODO: Stub
        scheduleToNextClock(new MemoryTimeAdvance(this, event.getProcess()));
        scheduleToNextClock(new MemoryIoOperationFinished(this, event.getProcess()));

    }

    private void handleMemoryIoOperation(MemoryIoOperation event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryTimeAdvance(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemory(MemoryFreeProcessMemory event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryFreeProcessMemoryFinished(this, event.getProcess()));
    }

    private void handleMemoryReserveProcessMemory(MemoryReserveProcessMemory event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryReserveProcessMemoryFinished(this, event.getProcess()));
    }

    private void handleMemoryPageFault(MemoryPageFault e) {
        //TODO: Stub
        scheduleToNextClock(new MemoryTimeAdvance(this, e.getProcess()));
    }

    private void handleMemoryPageHit(MemoryPageHit event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryProcessReady(this, event.getProcess()));
    }
}
