package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.memory.resources.PageFrameController;

public class PhysicalMemory extends AbstractSimEntity {

    PageFrameController mainMemory;
    PageFrameController swapFile;

    public PhysicalMemory(Simulation simulation, int mainMemorySize, int swapFileSize) {
        super(simulation);

        mainMemory = new PageFrameController(mainMemorySize);
        swapFile = new PageFrameController(swapFileSize);

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }

    public void processEvent(Event event) {
        switch (event) {
            case PageHit e -> handleMemoryPageHit(e);
            case PageFault e -> handleMemoryPageFault(e);

            case FreeProcessMemory e -> handleMemoryFreeProcessMemory(e);

            case IoOperation e -> handleMemoryIoOperation(e);
            case DiskOperation e -> handleMemoryDiskOperation(e);
            default -> throw new IllegalStateException(
                    "[PhysicalMemory] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryDiskOperation(DiskOperation event) {
        //TODO: can result in a MemoryTimeAdvance or a MemoryIoOperationFinished
        //TODO: Stub
        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
        scheduleToNextClock(new DiskOperationFinished(this, event.getProcess()));

    }

    private void handleMemoryIoOperation(IoOperation event) {
        //TODO: Stub
        scheduleToNextClock(new DiskOperation(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemory(FreeProcessMemory event) {
        //TODO: Stub
        scheduleToNextClock(new FreeProcessMemoryFinished(this, event.getProcess()));
    }

    private void handleMemoryPageFault(PageFault e) {
        //TODO: Stub
        scheduleToNextClock(new DiskOperation(this, e.getProcess()));
    }

    private void handleMemoryPageHit(PageHit event) {
        //TODO: Stub
        scheduleToNextClock(new ProcessReady(this, event.getProcess()));
    }
}
