package org.pampasim.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.resources.Process;

public class ProcessManager extends AbstractSimEntity {

    public ProcessManager(Simulation simulation) {
        super(simulation);

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(org.pampasim.events.External.Arrival.class, this);
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Ready.class, this);
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.RunPaused.class, this);

    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case org.pampasim.events.External.Arrival e -> handleProcessArrival(e);
            case org.pampasim.events.Process.Ready e -> handleProcessReady(e);
            case org.pampasim.events.Process.RunPaused e -> handleProcessRunPaused(e);
            default -> throw new IllegalStateException(
                    "[ProcessManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessArrival(org.pampasim.events.External.Arrival event) {
        scheduleToNextClock(new org.pampasim.events.Process.Allocate(this,
                new Process(getSimulation().getPidAllocator().assignPid(), event.getCreationData())));
    }

    private void handleProcessReady(org.pampasim.events.Process.Ready event) {
        event.getProcess().setState(Process.State.READY);
        scheduleToNextClock(new org.pampasim.events.Process.Schedule(this, event.getProcess()));
    }

    private void handleProcessRunPaused(org.pampasim.events.Process.RunPaused event) {
        Process process = event.getProcess();

        if (process.isFinished()) {
            process.setState(Process.State.TERMINATED);
            process.setEndTime(getSimulation().getRealClock().get());
            scheduleToNextClock(new org.pampasim.events.Process.End(this, process));
        } else {
            process.setState(Process.State.WAITING);
            scheduleToNextClock(new org.pampasim.events.Process.Schedule(this, event.getProcess()));
        }
    }
}
