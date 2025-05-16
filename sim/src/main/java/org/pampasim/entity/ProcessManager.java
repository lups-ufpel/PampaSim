package org.pampasim.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.events.*;

public class ProcessManager extends AbstractSimEntity {

    public ProcessManager(Simulation simulation) {
        super(simulation);

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(ProcessEvent.Arrival.class, this);
        simulation.getEventManager().addEventHandler(ProcessEvent.Ready.class, this);
        simulation.getEventManager().addEventHandler(ProcessEvent.RunPaused.class, this);

    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ProcessEvent.Arrival e -> handleProcessArrival(e);
            case ProcessEvent.Ready e -> handleProcessReady(e);
            case ProcessEvent.RunPaused e -> handleProcessRunPaused(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessArrival(ProcessEvent.Arrival event) {
        scheduleToNextClock(new ProcessEvent.Allocate(this, event.getProcess()));
    }

    private void handleProcessReady(ProcessEvent.Ready event) {
        event.getProcess().setReady();
        scheduleToNextClock(new ProcessEvent.Schedule(this, event.getProcess()));
    }

    private void handleProcessRunPaused(ProcessEvent.RunPaused event) {
        if (event.getProcess().isFinished()) {
            event.getProcess().setTerminated();
            scheduleToNextClock(new ProcessEvent.End(this, event.getProcess()));
        } else {
            scheduleToNextClock(new ProcessEvent.Schedule(this, event.getProcess()));
        }
    }
}