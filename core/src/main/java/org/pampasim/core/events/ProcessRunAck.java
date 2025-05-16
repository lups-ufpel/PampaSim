package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessRunAck extends ProcessEvent {
    public ProcessRunAck(SimEntity source, Process proc) {
        super(source, proc);
    }
}
