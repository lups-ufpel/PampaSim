package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessKill extends ProcessEvent {
    public ProcessKill(SimEntity source, Process proc) {
        super(source, proc);
    }
}
