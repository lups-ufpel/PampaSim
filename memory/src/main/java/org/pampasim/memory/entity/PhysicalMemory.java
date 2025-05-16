package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class PhysicalMemory extends AbstractSimEntity {
    public PhysicalMemory(Simulation simulation) {
        super(simulation);

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }

    public void processEvent(Event event) {
        switch (event) {
            case PageHit e -> handleMemoryPageHit(e);
            case PageFault e -> handleMemoryPageFault(e);

            case ReserveProcessMemory e -> handleMemoryReserveProcessMemory(e);

            case FreeProcessMemory e -> handleMemoryFreeProcessMemory(e);

            case IoOperation e -> handleMemoryIoOperation(e);
            case TimeAdvance e -> handleMemoryTimeAdvance(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryTimeAdvance(TimeAdvance event) {
        //TODO: can result in a MemoryTimeAdvance or a MemoryIoOperationFinished
        //TODO: Stub
        scheduleToNextClock(new TimeAdvance(this, event.getProcess()));
        scheduleToNextClock(new IoOperationFinished(this, event.getProcess()));

    }

    private void handleMemoryIoOperation(IoOperation event) {
        //TODO: Stub
        scheduleToNextClock(new TimeAdvance(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemory(FreeProcessMemory event) {
        //TODO: Stub
        scheduleToNextClock(new FreeProcessMemoryFinished(this, event.getProcess()));
    }

    private void handleMemoryReserveProcessMemory(ReserveProcessMemory event) {
        //TODO: Stub
        scheduleToNextClock(new ReserveProcessMemoryFinished(this, event.getProcess()));
    }

    private void handleMemoryPageFault(PageFault e) {
        //TODO: Stub
        scheduleToNextClock(new TimeAdvance(this, e.getProcess()));
    }

    private void handleMemoryPageHit(PageHit event) {
        //TODO: Stub
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }
}
