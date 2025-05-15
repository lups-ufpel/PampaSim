package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRun extends ProcessEvent {
    public ProcessRun(SimEntity source, Process proc) {
        super(source, proc);
    }
}
