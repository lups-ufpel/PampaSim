package org.pampasim.SimEntity;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;

public class ProcessManager extends PampaSimEntity {

    public ProcessManager(Simulation simulation) {
        super(simulation);

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);

    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ProcessArrival e -> handleProcessArrival(e);
            case ProcessReady e -> handleProcessReady(e);
            case ProcessRunPaused e -> handleProcessRunPaused(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessArrival(ProcessArrival event) {
        scheduleToNextClock(new ProcessAllocate(this, event.getProcess()));
    }

    private void handleProcessReady(ProcessReady event) {
        event.getProcess().setReady();
        scheduleToNextClock(new ProcessSchedule(this, event.getProcess()));
    }

    private void handleProcessRunPaused(ProcessRunPaused event) {
        if (event.getProcess().isFinished()) {
            event.getProcess().setTerminated();
            scheduleToNextClock(new ProcessEnd(this, event.getProcess()));
        } else {
            scheduleToNextClock(new ProcessSchedule(this, event.getProcess()));
        }
    }
}