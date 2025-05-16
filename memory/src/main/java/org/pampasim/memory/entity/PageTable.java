package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class PageTable extends AbstractSimEntity {

    //TODO: Add data structures to store the page table entries for each processs

    public PageTable(Simulation simulation) {
        super(simulation);

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }

    public void processEvent(Event event) {
        switch (event) {
            case Allocate e -> handleMemoryAllocate(e);
            case DeletePageTableEntry e -> handleMemoryDeletePageTableEntry(e);
            case TlbNoTranslation e -> handleMemoryTlbNoTranslation(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryAllocate(Allocate event) {
        //TODO: Stub
        scheduleToNextClock(new AllocateFinished(this, event.getProcess()));
    }

    private void handleMemoryDeletePageTableEntry(DeletePageTableEntry event) {
        //TODO: Stub
        scheduleToNextClock(new FreeProcessMemory(this, event.getProcess()));
    }

    private void handleMemoryTlbNoTranslation(TlbNoTranslation event) {
        //TODO: can result in a MemoryPageHit or a MemoryPageFault event
        //TODO: Stub
        scheduleToNextClock(new PageHit(this, event.getProcess()));
        scheduleToNextClock(new PageFault(this, event.getProcess()));
    }

}
