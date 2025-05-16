package org.pampasim.SimEntity.Memory;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import org.pampasim.SimCore.events.Memory.*;
import org.pampasim.SimEntity.PampaSimEntity;

public class PageTable extends PampaSimEntity {

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
