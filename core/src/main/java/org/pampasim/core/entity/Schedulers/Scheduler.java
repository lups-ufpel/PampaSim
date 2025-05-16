package org.pampasim.core.entity.Schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.entity.PampaSimEntity;
import org.pampasim.core.resources.Process;

public abstract class Scheduler extends PampaSimEntity {
    boolean processEnRoute;
    protected Process lastRunProcess;

    public Scheduler(Simulation simulation) {
        super(simulation);
        processEnRoute = false;
        lastRunProcess = null;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(ProcessSchedule.class, this);
        simulation.getEventManager().addEventHandler(ProcessRunAck.class, this);
    }

    @Override
    public void managedRun() {
        buffer.forEach(this::processEvent);
        buffer.clear();

        if (this.lastProcessFinished() && !processEnRoute) {
            scheduleNextProcess();
        }
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case ProcessSchedule e -> handleProcessSchedule(e);
            case ProcessRunAck e -> handleProcessRunAck(e);
            default -> throw new IllegalStateException("[Scheduler] Evento do tipo " + event.getClass().getSimpleName() + " não pode ser tratado, evento serial: " + event.getSerial());
        }
    }

    protected abstract void handleProcessSchedule(ProcessSchedule event);
    protected void handleProcessRunAck(ProcessRunAck event) {
        event.getProcess().notifyListenersOnUpdate();
        processEnRoute = false;
        lastRunProcess = event.getProcess();
    }

    protected abstract Process nextProcessToSchedule();

    // kept private so we are sure the books are up to date
    private void scheduleNextProcess() {
        if (processEnRoute) { return; }
        Process proc = nextProcessToSchedule();
        if (proc == null) { return; }
        scheduleToNextClock(new ProcessDispatch(this, proc));
        processEnRoute = true;
    }

    protected boolean lastProcessFinished() {
        var proc = lastRunProcess;
        return proc == null || proc.getState() != Process.State.RUNNING;
    }
}
