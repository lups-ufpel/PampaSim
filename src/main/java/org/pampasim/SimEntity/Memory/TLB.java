package org.pampasim.SimEntity.Memory;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import org.pampasim.SimCore.events.Memory.*;
import org.pampasim.SimEntity.PampaSimEntity;

public class TLB extends PampaSimEntity {
    //TODO: TLB should clear when there is a context switch
    public TLB (Simulation simulation) {
        super(simulation);

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }

    public void processEvent(Event event) {
        switch (event) {
            case MemoryDeleteTlbEntry e -> handleMemoryDeleteTlbEntry(e);
            case MemoryTranslateVirtualAddress e -> handleTranslateVirtualAddress(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryDeleteTlbEntry(MemoryDeleteTlbEntry event) {
        //TODO: Stub
        scheduleToNextClock(new MemoryFreeProcessMemory(this, event.getProcess()));
    }

    private void handleTranslateVirtualAddress(MemoryTranslateVirtualAddress event) {
        //TODO: Stub
        //TODO: can result in a MemoryTlbNoTranslation, MemoryPageHit or MemoryPageFault event
        scheduleToNextClock(new MemoryTlbNoTranslation(this, event.getProcess()));
        scheduleToNextClock(new MemoryPageFault(this, event.getProcess()));
        scheduleToNextClock(new MemoryPageHit(this, event.getProcess()));
    }
}