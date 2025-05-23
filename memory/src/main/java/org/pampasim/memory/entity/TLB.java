package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;

public class TLB extends AbstractSimEntity {
    //TODO: TLB should clear when there is a context switch
    public TLB (Simulation simulation) {
        super(simulation);

        simulation.getEventManager().addEventHandler(DeleteTlbEntry.class, this);
        simulation.getEventManager().addEventHandler(TranslateVirtualAddress.class, this);
    }

    public void processEvent(Event event) {
        switch (event) {
            case DeleteTlbEntry e -> handleMemoryDeleteTlbEntry(e);
            case TranslateVirtualAddress e -> handleTranslateVirtualAddress(e);
            default -> throw new IllegalStateException(
                    "[TLB] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryDeleteTlbEntry(DeleteTlbEntry event) {
        //TODO: Stub
        scheduleToNextClock(new FreeProcessMemory(this, event.getProcess()));
    }

    private void handleTranslateVirtualAddress(TranslateVirtualAddress event) {
        //TODO: Stub
        //TODO: can result in a TlbNoTranslation, PageHit or PageFault event
        scheduleToNextClock(new TlbNoTranslation(this, event.getProcess()));
        scheduleToNextClock(new PageFault(this, event.getProcess()));
        scheduleToNextClock(new PageHit(this, event.getProcess()));
    }
}