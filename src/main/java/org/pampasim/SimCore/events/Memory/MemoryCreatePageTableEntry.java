package org.pampasim.SimCore.events.Memory;

import org.pampasim.SimCore.events.ProcessEvent;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class MemoryCreatePageTableEntry extends ProcessEvent {
    public MemoryCreatePageTableEntry(SimEntity source, Process proc) {
        super(source, proc);
    }
}
