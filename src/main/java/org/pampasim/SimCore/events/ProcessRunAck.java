package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRunAck extends ProcessEvent {
    public ProcessRunAck(SimEntity source, Process proc) {
        super(source, proc);
    }
}
