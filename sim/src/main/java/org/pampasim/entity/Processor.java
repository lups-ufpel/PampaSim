package org.pampasim.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.core.resources.Process;
import org.pampasim.core.resources.ProcessorCore;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Processor extends AbstractSimEntity {
    private final ProcessorCore core;
    private boolean preemption;

    public Processor(Simulation simulation, ProcessorCore core) {
        super(simulation);

        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.core = core;
        this.preemption = false;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Run.class, this);
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Dispatch.class, this);
        //simulation.getEventManager().addEventHandler(org.pampasim.events.Process.RunContinue.class, this);
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Preemption.class, this);
    }

    @Override
    public void managedRun() {
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll()); // Order: PREEMPT_PROCESS -> RUN_PROCESS_CONTINUE -> RUN_PROCESS
        }
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case org.pampasim.events.Process.Dispatch e -> handleProcessDispatch(e);
            case org.pampasim.events.Process.Run e -> handleProcessRun(e);
            case org.pampasim.events.Process.Preemption e -> handleProcessPreemption(e);
            default -> throw new IllegalStateException(
                    "[Scheduler] Evento do tipo "
                    + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessDispatch(org.pampasim.events.Process.Dispatch event) {
        Process process = event.getProcess();
        //getSimulation().scheduleToNextClock(new org.pampasim.events.Process.RunAck(this, process));
        process.setRunning();
        core.setStatus(ProcessorCore.Status.BUSY);
        logInfo("Início da execução do processo de identificador:" + process.getPidString());
        preemption = false;
        core.execute(process);
        getSimulation().scheduleToNextClock(new org.pampasim.events.Process.Load(this, process));
    }
    private void handleProcessRun(org.pampasim.events.Process.Run event) {
        Process process = event.getProcess();
        if (process.isFinished() || process.getBurstTime() <= 0 || preemption) {
            core.setStatus(ProcessorCore.Status.FREE);
            getSimulation().scheduleToNextClock(new org.pampasim.events.Process.RunPaused(this, process));
            process.setSuspended();
            logInfo("Fim do turno de execução do processo de identificador:" + process.getPidString());
        } else {
            logInfo("Continuação da Execução do processo de identificador:" + process.getPidString());
            core.execute(process);
            getSimulation().scheduleToNextClock(new org.pampasim.events.Process.Load(this, process));
        }
    }
    private void handleProcessPreemption(org.pampasim.events.Process.Preemption event) {
        preemption = true;
        logInfo("Interrupção da execução de processo de identificador:" + event.getProcess().getPidString());
    }
    public boolean isFree() {
        return ProcessorCore.Status.FREE == this.core.getStatus();
    }

    private int getEventPriority(Event event) {
        return switch (event) {
            case org.pampasim.events.Process.Preemption _e -> 1;   // Highest priority
            case org.pampasim.events.Process.Run _e -> 2;
            case org.pampasim.events.Process.Dispatch _e -> 3;       // Lowest priority
            default -> Integer.MAX_VALUE;
        };
    }
}