package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRunPaused extends ProcessEvent {
    public ProcessRunPaused(SimEntity source, Process proc) {
        super(source, proc);
    }
}
