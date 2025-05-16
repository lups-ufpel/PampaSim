package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessRun extends ProcessEvent {
    public ProcessRun(SimEntity source, Process proc) {
        super(source, proc);
    }
}
