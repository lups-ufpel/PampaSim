package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRunPaused extends ProcessEvent {
    public ProcessRunPaused(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
