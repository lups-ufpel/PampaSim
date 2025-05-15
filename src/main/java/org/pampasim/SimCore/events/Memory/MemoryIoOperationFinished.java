package org.pampasim.SimCore.events.Memory;

import org.pampasim.SimCore.events.ProcessEvent;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class MemoryIoOperationFinished extends ProcessEvent {
    public MemoryIoOperationFinished(SimEntity source, Process proc) {
        super(source, proc);
    }
}

