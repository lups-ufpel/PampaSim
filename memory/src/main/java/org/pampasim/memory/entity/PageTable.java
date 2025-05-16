package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.memory.events.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class PageTable extends AbstractSimEntity {

    //TODO: Add data structures to store the page table entries for each processs

    public PageTable(Simulation simulation) {
        super(simulation);
    }

    public void processEvent(Event event) {
        switch (event) {
            case MemoryCreatePageTableEntry e -> handleMemoryCreatePageTableEntry(e);
            case MemoryDeletePageTableEntry e -> handleMemoryDeletePageTableEntry(e);
            case MemoryTlbNoTranslation e -> handleMemoryTlbNoTranslation(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryCreatePageTableEntry(MemoryCreatePageTableEntry event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryReserveProcessMemory(this, event.getProcess()));
    }

    private void handleMemoryDeletePageTableEntry(MemoryDeletePageTableEntry event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryDeleteTlbEntry(this, event.getProcess()));
    }

    private void handleMemoryTlbNoTranslation(MemoryTlbNoTranslation event) {
        //TODO: can result in a MemoryPageHit or a MemoryPageFault event
        //TODO: Stub
        scheduleToNextClock(new MemoryPageHit(this, event.getProcess()));
        scheduleToNextClock(new MemoryPageFault(this, event.getProcess()));
    }

}
