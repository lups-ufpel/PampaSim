package org.pampasim.core.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.resources.Process;
import org.pampasim.core.resources.ProcessorCore;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Processor extends PampaSimEntity {
    private final ProcessorCore core;
    private boolean preemption;

    public Processor(Simulation simulation, ProcessorCore core) {
        super(simulation);

        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.core = core;
        this.preemption = false;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(ProcessRun.class, this);
        simulation.getEventManager().addEventHandler(ProcessRunContinue.class, this);
        simulation.getEventManager().addEventHandler(ProcessPreemption.class, this);
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
            case ProcessRun e -> handleProcessRun(e);
            case ProcessRunContinue e -> handleProcessRunContinue(e);
            case ProcessPreemption e -> handleProcessPreemption(e);
            default -> throw new IllegalStateException(
                    "[Scheduler] Evento do tipo "
                    + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessRun(ProcessRun event) {
        Process process = event.getProcess();
        getSimulation().scheduleToNextClock(new ProcessRunAck(this, process));
        process.setRunning();
        core.setStatus(ProcessorCore.Status.BUSY);
        logInfo("Início da execução do processo de identificador:" + process.getPid());
        preemption = false;
        core.execute(process);
        getSimulation().scheduleToNextClock(new ProcessRunContinue(this, process));
    }
    private void handleProcessRunContinue(ProcessRunContinue event) {
        Process process = event.getProcess();
        if (process.isFinished() || process.getBurstTime() <= 0 || preemption) {
            core.setStatus(ProcessorCore.Status.FREE);
            getSimulation().scheduleToNextClock(new ProcessRunPaused(this, process));
            process.setSuspended();
            logInfo("Fim do turno de execução do processo de identificador:" + process.getPid());
        } else {
            logInfo("Continuação da Execução do processo de identificador:" + process.getPid());
            core.execute(process);
            getSimulation().scheduleToNextClock(new ProcessRunContinue(this, process));
        }
    }
    private void handleProcessPreemption(ProcessPreemption event) {
        preemption = true;
        logInfo("Interrupção da execução de processo de identificador:" + event.getProcess().getPid());
    }
    public boolean isFree() {
        return ProcessorCore.Status.FREE == this.core.getStatus();
    }

    private int getEventPriority(Event event) {
        return switch (event) {
            case ProcessPreemption _e -> 1;   // Highest priority
            case ProcessRunContinue _e -> 2;
            case ProcessRun _e -> 3;       // Lowest priority
            default -> Integer.MAX_VALUE;
        };
    }
}