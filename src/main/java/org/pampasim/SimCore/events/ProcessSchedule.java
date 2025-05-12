package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.SimEntity;
import org.pampasim.SimResources.Process;

public class ProcessSchedule extends ProcessEvent {
    public ProcessSchedule(SimEntity source, Process proc) {
        super(source, proc);
    }
}
