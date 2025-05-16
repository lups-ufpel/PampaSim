package org.pampasim.memory.events;

import org.pampasim.core.events.ProcessEvent;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class MemoryFreeProcessMemoryFinished  extends ProcessEvent {
    public MemoryFreeProcessMemoryFinished(SimEntity source, Process proc) {
        super(source, proc);
    }
}

