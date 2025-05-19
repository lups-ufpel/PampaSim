package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.memory.resources.PageTableEntry;
import org.pampasim.memory.resources.ProcessPageTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageTableManager extends AbstractSimEntity {

    private final Map<Long, Map<Long, ProcessPageTable>> pageTableMap;

    public PageTableManager(Simulation simulation) {
        super(simulation);
        pageTableMap = new HashMap<>();

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
                    "[PageTableManager] Evento do tipo " + event.getClass().getSimpleName()
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
