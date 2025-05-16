package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessReady extends ProcessEvent {
    public ProcessReady(SimEntity source, Process proc) {
        super(source, proc);
    }
}
