package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessEvent extends AbstractEvent {
    public ProcessEvent(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
    public Process getProcess() {
        return (Process)getData();
    }
}

