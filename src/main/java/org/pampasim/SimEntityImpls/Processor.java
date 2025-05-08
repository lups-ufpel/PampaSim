package org.pampasim.SimEntityImpls;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.ProcessorCore;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Processor extends EntityImpl {
    private final ProcessorCore core;

    public enum State {
        // codegen states
    }
    private State state;
    public Processor(Simulation simulation, ProcessorCore core) {
        super(simulation);

        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.core = core;

        // codegen register handlers
    }

    @Override
    public void run() {
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll()); // Order: PREEMPT_PROCESS -> RUN_PROCESS_CONTINUE -> RUN_PROCESS
        }
    }

    // codegen processEvent

    private void onRunProcess(PampaSimEvent event) {}
    private void onRunProcessContinue(PampaSimEvent event) {}
    private void onPreemptProcess(PampaSimEvent event) {}
    private int getEventPriority(PampaSimEvent event) {
        return switch (event.getEventType()) {
            case PREEMPT_PROCESS -> 1;   // Highest priority
            case RUN_PROCESS_CONTINUE -> 2;
            case RUN_PROCESS -> 3;       // Lowest priority
            default -> Integer.MAX_VALUE;
        };
    }
    public boolean isFree() {
        return ProcessorCore.Status.FREE == this.core.getStatus();
    }
}