package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessArrival extends ProcessEvent {
    public ProcessArrival(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
