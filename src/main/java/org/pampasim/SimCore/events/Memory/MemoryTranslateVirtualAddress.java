package org.pampasim.SimCore.events.Memory;

import org.pampasim.SimCore.events.ProcessEvent;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class MemoryTranslateVirtualAddress extends ProcessEvent {
    public MemoryTranslateVirtualAddress(SimEntity source, Process proc) {
        super(source, proc);
    }
}
