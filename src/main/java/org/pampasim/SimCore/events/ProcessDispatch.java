package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessDispatch extends ProcessEvent {
    public ProcessDispatch(PampaSimEntity source, Process proc) {
        super(source, proc);
    }
}
