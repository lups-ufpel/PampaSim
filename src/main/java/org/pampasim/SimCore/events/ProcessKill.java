package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessKill extends ProcessEvent {
    public ProcessKill(SimEntity source, Process proc) {
        super(source, proc);
    }
}
