package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessEnd extends ProcessEvent {
    public ProcessEnd(SimEntity source, Process proc) {
        super(source, proc);
    }
}
