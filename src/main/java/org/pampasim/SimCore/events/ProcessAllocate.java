package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessAllocate extends ProcessEvent {
    public ProcessAllocate(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
