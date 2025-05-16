package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessArrival extends ProcessEvent {
    public ProcessArrival(SimEntity source, Process proc) {
        super(source, proc);
    }
}
