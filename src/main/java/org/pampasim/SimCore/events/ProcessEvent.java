package org.pampasim.SimCore.events;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimResources.Process;

public class ProcessEvent extends AbstractEvent {
    private Process process;
    public ProcessEvent(PampaSimEntity source, Process proc) {
        super(source);
        process = proc;
    }
    // translation constructor, may need to become a factory method
    // to support runtime data incompatibility checks
    public ProcessEvent(Event e) {
        // this data cast may fail!
        this(e.getSource(), (Process)e.getData());
    }

    public Process getProcess() {
        return process;
    }

    public Object getData() {
        return (Object)getProcess();
    }
}

