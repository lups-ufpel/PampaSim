package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessEnd extends ProcessEvent {
    public ProcessEnd(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
