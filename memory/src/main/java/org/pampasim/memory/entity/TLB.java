package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.memory.events.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class TLB extends AbstractSimEntity {
    //TODO: TLB should clear when there is a context switch
    public TLB (Simulation simulation) {
        super(simulation);
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