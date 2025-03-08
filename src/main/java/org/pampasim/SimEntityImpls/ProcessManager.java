package org.pampasim.SimEntityImpls;

import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimEntity.PampaSimEntity;

public class ProcessManager extends EntityImpl {
    public enum State {
        // codegen states
    }

    private State state;
    public ProcessManager(Simulation simulation) {
        super(simulation);

        // codegen register handlers
    }

    // codegen processEvent

    private void onProcessArrival(PampaSimEvent event) {
        scheduleToNextClock(event.changeType(EventType.ALLOCATE_PROCESS));
    }

    private void onReadyProcess(PampaSimEvent event) {
        scheduleToNextClock(event.changeType(EventType.SCHEDULE_PROCESS));
    }

    private void onProcessExecutionEnd(PampaSimEvent event) {
        if (event.getProcess().isFinished()) {
            scheduleToNextClock(event.changeType(EventType.END_PROCESS));
        } else {
            scheduleToNextClock(event.changeType(EventType.SCHEDULE_PROCESS));
        }
    }
}