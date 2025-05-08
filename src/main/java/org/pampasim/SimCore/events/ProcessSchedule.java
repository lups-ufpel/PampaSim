package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessSchedule extends ProcessEvent {
    public ProcessSchedule(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
