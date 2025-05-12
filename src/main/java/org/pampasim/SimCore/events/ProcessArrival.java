package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessArrival extends ProcessEvent {
    public ProcessArrival(SimEntity source, Process proc) {
        super(source, proc);
    }
}
