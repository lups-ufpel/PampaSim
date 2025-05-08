package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessRunAck extends ProcessEvent {
    public ProcessRunAck(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
