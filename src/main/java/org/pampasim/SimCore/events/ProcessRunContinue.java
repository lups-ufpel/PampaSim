package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRunContinue extends ProcessEvent {
    public ProcessRunContinue(SimEntity source, Process proc) {
        super(source, proc);
    }
}
