package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessKill extends ProcessEvent {
    public ProcessKill(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
