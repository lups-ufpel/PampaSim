package org.pampasim.SimCore.events.Memory;

import org.pampasim.SimCore.events.ProcessEvent;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class MemoryPageHit extends ProcessEvent {
    public MemoryPageHit(SimEntity source, Process proc) {
        super(source, proc);
    }
}