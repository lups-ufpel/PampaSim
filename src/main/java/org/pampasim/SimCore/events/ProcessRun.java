package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRun extends ProcessEvent {
    public ProcessRun(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
