package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRunContinue extends ProcessEvent {
    public ProcessRunContinue(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
