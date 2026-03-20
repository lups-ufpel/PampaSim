package org.pampasim.entity.schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.resources.Process;

import java.util.Collection;
import java.util.stream.Stream;

// TODO: write a suite of tests that assert the invariants as described below to validate foreign schedulers

/// Provides a managedRun() override that schedules the next process when appropriate,
/// a shouldRunNextTick() that respects the queue and
/// handles process state transitions and event bindings for the core simulation.
/// Expects concrete Process.Schedule handler and nextProcessToSchedule(),
/// short and long descriptions, and if you decide to forego the default processQueue,
/// a correctly implemented incrementWaitingTimes(), shouldRunNextTick(), and getScheduledProcesses().
public abstract class Scheduler extends AbstractSimEntity {
    private final Logger LOGGER = LogManager.getLogger(Scheduler.class);
    protected Process lastRunProcess;
    protected Collection<Process> processQueue;

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

    @Override
    public boolean shouldRunNextTick() {
        return super.shouldRunNextTick()
                || (this.lastProcessFinished()
                    && (this.processQueue != null && !this.processQueue.isEmpty()));
    }

    protected abstract void handleProcessSchedule(org.pampasim.events.Process.Schedule event);

    protected abstract Process nextProcessToSchedule();

    public abstract String shortDescription();
    public abstract String longDescription();

    // kept private so we are sure the books are up to date
    private void scheduleNextProcess() {
        Process proc = nextProcessToSchedule();
        if (proc == null) { return; }
        proc.setState(Process.State.SCHEDULED);
        lastRunProcess = proc;
        scheduleToNextClock(new org.pampasim.events.Process.Dispatch(this, proc));
    }

    public Stream<Process> getScheduledProcesses() {
        return this.processQueue.stream();
    }

    protected boolean lastProcessFinished() {
        var proc = lastRunProcess;
        return proc == null || !( proc.getState() == Process.State.RUNNING || proc.getState() == Process.State.SCHEDULED);
    }

    public void incrementWaitingTimes() {
        processQueue.forEach(Process::forwardWaitingTime);
    }
}
