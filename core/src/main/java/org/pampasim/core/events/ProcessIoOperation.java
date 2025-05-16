package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessIoOperation extends ProcessEvent {
    public ProcessIoOperation(SimEntity source, Process proc) {
        super(source, proc);
    }
}
