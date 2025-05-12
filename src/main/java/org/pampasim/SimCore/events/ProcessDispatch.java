package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessDispatch extends ProcessEvent {
    public ProcessDispatch(SimEntity source, Process proc) {
        super(source, proc);
    }
}
