package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.events.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.resources.Process;

public abstract class Scheduler extends AbstractSimEntity {
    protected Process lastRunProcess;

    public Scheduler(Simulation simulation) {
        super(simulation);
        lastRunProcess = null;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Schedule.class, this);
    }

    @Override
    public void managedRun() {
        buffer.forEach(this::processEvent);
        buffer.clear();

        if (this.lastProcessFinished()) {
            scheduleNextProcess();
        }
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case org.pampasim.events.Process.Schedule e -> handleProcessSchedule(e);
            default -> throw new IllegalStateException("[Scheduler] Evento do tipo " + event.getClass().getSimpleName() + " não pode ser tratado, evento serial: " + event.getSerial());
        }
    }

    protected abstract void handleProcessSchedule(org.pampasim.events.Process.Schedule event);

    protected abstract Process nextProcessToSchedule();

    // kept private so we are sure the books are up to date
    private void scheduleNextProcess() {
        Process proc = nextProcessToSchedule();
        if (proc == null) { return; }
        proc.setState(Process.State.SCHEDULED);
        lastRunProcess = proc;
        scheduleToNextClock(new org.pampasim.events.Process.Dispatch(this, proc));

    }

    protected boolean lastProcessFinished() {
        var proc = lastRunProcess;
        return proc == null || !( proc.getState() == Process.State.RUNNING || proc.getState() == Process.State.SCHEDULED);
    }
}
