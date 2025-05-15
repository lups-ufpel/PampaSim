package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessAllocate extends ProcessEvent {
    public ProcessAllocate(SimEntity source, Process proc) {
        super(source, proc);
    }
}
