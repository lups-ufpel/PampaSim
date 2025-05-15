package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessPreemption extends ProcessEvent {
    public ProcessPreemption(SimEntity source, Process proc) {
        super(source, proc);
    }
}
