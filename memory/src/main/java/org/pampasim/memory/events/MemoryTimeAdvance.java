package org.pampasim.memory.events;

import org.pampasim.core.events.ProcessEvent;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class MemoryTimeAdvance extends ProcessEvent {
    public MemoryTimeAdvance(SimEntity source, Process proc) {
        super(source, proc);
    }
}