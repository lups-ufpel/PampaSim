package org.pampasim.core.events;

import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.resources.Process;

public class ProcessSchedule extends ProcessEvent {
    public ProcessSchedule(SimEntity source, Process proc) {
        super(source, proc);
    }
}
