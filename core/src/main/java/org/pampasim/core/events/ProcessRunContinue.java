package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessRunContinue extends ProcessEvent {
    public ProcessRunContinue(SimEntity source, Process proc) {
        super(source, proc);
    }
}
