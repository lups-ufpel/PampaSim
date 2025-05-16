package org.pampasim.memory.events;

import org.pampasim.core.events.ProcessEvent;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class MemoryTlbNoTranslation extends ProcessEvent {
    public MemoryTlbNoTranslation(SimEntity source, Process proc) {
        super(source, proc);
    }
}