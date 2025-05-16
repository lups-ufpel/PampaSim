package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessDispatch extends ProcessEvent {
    public ProcessDispatch(SimEntity source, Process proc) {
        super(source, proc);
    }
}
