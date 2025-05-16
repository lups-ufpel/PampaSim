package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessPreemption extends ProcessEvent {
    public ProcessPreemption(SimEntity source, Process proc) {
        super(source, proc);
    }
}
