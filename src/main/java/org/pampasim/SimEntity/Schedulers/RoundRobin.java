package org.pampasim.SimEntity.Schedulers;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.Process;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.Queue;

public class RoundRobin extends Scheduler {
    public Queue<Process> rrQueue;

    public RoundRobin(Simulation simulation) {
        super(simulation);
        rrQueue = new ArrayDeque<>();
    }

    @Override
    protected void handleScheduleProcess(PampaSimEvent event) {
        rrQueue.add(event.getProcess());
    }

    @Override
    protected void scheduleNextProcess() {
        if (processEnRoute) { return; }
        scheduleToNextClock();
    }
}
