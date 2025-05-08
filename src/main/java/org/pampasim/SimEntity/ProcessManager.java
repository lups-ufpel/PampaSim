package org.pampasim.SimEntity;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.EventType;

public class ProcessManager extends PampaSimEntity {

    public ProcessManager(Simulation simulation) {
        super(simulation);

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(EventType.PROCESS_ARRIVAL, this);
        simulation.getEventManager().addEventHandler(EventType.READY_PROCESS, this);
        simulation.getEventManager().addEventHandler(EventType.PROCESS_EXECUTION_END, this);

    }

    @Override
    public void processEvent(PampaSimEvent event) {
        switch (event.getEventType()) {
            case PROCESS_ARRIVAL -> handleProcessArrival(event);
            case READY_PROCESS -> handleReadyProcess(event);
            case PROCESS_EXECUTION_END -> handleProcessExecutionEnd(event);
            default -> throw new IllegalStateException("[ProcessManager] Evento do tipo " + event.getEventType() + " não pode ser tratado, evento serial: " + event.getSerial());
        }
    }

    private void handleProcessArrival(PampaSimEvent event) {
        scheduleToNextClock(event.changeType(EventType.ALLOCATE_PROCESS));
    }

    private void handleReadyProcess(PampaSimEvent event) {
        event.getProcess().setReady();
        scheduleToNextClock(event.changeType(EventType.SCHEDULE_PROCESS));
    }

    private void handleProcessExecutionEnd(PampaSimEvent event) {
        if (event.getProcess().isFinished()) {
            event.getProcess().setTerminated();
            scheduleToNextClock(event.changeType(EventType.END_PROCESS));
        } else {
            scheduleToNextClock(event.changeType(EventType.SCHEDULE_PROCESS));
        }
    }
}