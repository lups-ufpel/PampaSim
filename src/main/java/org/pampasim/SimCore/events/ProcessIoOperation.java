package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessIoOperation extends ProcessEvent {
    public ProcessIoOperation(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
