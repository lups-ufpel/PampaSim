package org.pampasim.SimCore.events.Memory;

import org.pampasim.SimCore.events.ProcessEvent;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class MemoryTlbMiss extends ProcessEvent {
    public MemoryTlbMiss(SimEntity source, Process proc) {
        super(source, proc);
    }
}