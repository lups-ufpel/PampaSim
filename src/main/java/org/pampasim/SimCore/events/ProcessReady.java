package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessReady extends ProcessEvent {
    public ProcessReady(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
