package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessReady extends ProcessEvent {
    public ProcessReady(SimEntity source, Process proc) {
        super(source, proc);
    }
}
