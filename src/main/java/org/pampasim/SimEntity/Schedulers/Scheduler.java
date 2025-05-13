package org.pampasim.SimEntity.Schedulers;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.*;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimResources.Process;

public abstract class Scheduler extends PampaSimEntity {
    boolean processEnRoute;
    protected Process lastRunningProcess;

    public Scheduler(Simulation simulation) {
        super(simulation);
        processEnRoute = false;
        lastRunningProcess = null;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(ProcessSchedule.class, this);
        simulation.getEventManager().addEventHandler(ProcessRunAck.class, this);
    }

    @Override
    public void innerRun() {
        buffer.forEach(this::processEvent);
        buffer.clear();

        Processor cpu = getSimulation().getEntity(Processor.class);

        if (cpu.isFree() && !processEnRoute) {
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
        lastRunningProcess = event.getProcess();
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
}
