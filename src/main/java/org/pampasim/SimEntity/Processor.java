package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.PampaSimEventID;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.EventType;
import org.pampasim.SimResources.Process;
import org.pampasim.SimResources.ProcessorCore;

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
        simulation.getEventManager().addEventHandler(EventType.RUN_PROCESS, this);
        simulation.getEventManager().addEventHandler(EventType.RUN_PROCESS_CONTINUE, this);
        simulation.getEventManager().addEventHandler(EventType.PREEMPT_PROCESS, this);
    }

    @Override
    public void processEventsInBuffer() {
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll()); // Order: PREEMPT_PROCESS -> RUN_PROCESS_CONTINUE -> RUN_PROCESS
        }
    }


    @Override
    public void processEvent(PampaSimEvent event) {
        switch (event.getEventType()) {
            case RUN_PROCESS -> handleRunProcess(event);
            case RUN_PROCESS_CONTINUE -> handleRunProcessContinue(event);
            case PREEMPT_PROCESS -> handlePreemptProcess(event);
            default -> throw new IllegalStateException("[Scheduler] Evento do tipo " + event.getEventType() + " não pode ser tratado, evento serial: " + event.getSerial());
        }
    }

    private void handleRunProcess(PampaSimEvent event) {
        getSimulation().scheduleToNextClock(event.changeType(EventType.RUN_PROCESS_ACK));
        Process process = event.getProcess();
        core.setStatus(ProcessorCore.Status.BUSY);
        System.out.println("[Processador] Execução de processo de identificador:" + process.getPid());
        core.execute(event.getProcess());
        if (process.isFinished() || process.getBurstTime() == 0) {
            core.setStatus(ProcessorCore.Status.FREE);
            getSimulation().scheduleToNextClock(event.changeType(EventType.PROCESS_EXECUTION_END));
            System.out.println("[Processador] Fim do turno de execução de processo de identificador:" + process.getPid());
        } else {
            getSimulation().scheduleToNextClock(event.changeType(EventType.RUN_PROCESS_CONTINUE));
        }
    }
    private void handleRunProcessContinue(PampaSimEvent event) {
        Process process = event.getProcess();
        core.setStatus(ProcessorCore.Status.BUSY);
        System.out.println("[Processador] Continuação da Execução de processo de identificador:" + process.getPid());
        core.execute(event.getProcess());
        if (process.isFinished() || process.getBurstTime() == 0 || preemption) {
            core.setStatus(ProcessorCore.Status.FREE);
            preemption = false;
            getSimulation().scheduleToNextClock(event.changeType(EventType.PROCESS_EXECUTION_END));
            System.out.println("[Processador] Fim do turno de execução de processo de identificador:" + process.getPid());
        } else {
            getSimulation().scheduleToNextClock(event.changeType(EventType.RUN_PROCESS_CONTINUE));
        }
    }
    private void handlePreemptProcess(PampaSimEvent event) {
        preemption = true;
        System.out.println("[Processador] Interrupção da execução de processo de identificador:" + event.getProcess().getPid());
    }
    public boolean isFree() {
        return this.core.isFree();
    }

    private int getEventPriority(PampaSimEvent event) {
        return switch (event.getEventType()) {
            case PREEMPT_PROCESS -> 1;   // Highest priority
            case RUN_PROCESS_CONTINUE -> 2;
            case RUN_PROCESS -> 3;       // Lowest priority
            default -> Integer.MAX_VALUE;
        };
    }
}