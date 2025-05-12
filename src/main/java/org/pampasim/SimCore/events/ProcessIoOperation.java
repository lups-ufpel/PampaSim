package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessIoOperation extends ProcessEvent {
    public ProcessIoOperation(SimEntity source, Process proc) {
        super(source, proc);
    }
}
