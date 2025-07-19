package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.Comparator;
import java.util.PriorityQueue;

// WARN: tightly coupled with baseclass thru the processEnRoute flag
public abstract class RankingScheduler extends Scheduler {
    PriorityQueue<Process> readyList;

    public RankingScheduler(Simulation simulation) {
        super(simulation);
        this.processQueue = new PriorityQueue<>(processRankingAlgorithm());
    }

    public abstract Comparator<Process> processRankingAlgorithm();

    @Override
    public boolean shouldRunNextTick() {
        return super.shouldRunNextTick()
                || (this.lastProcessFinished() && !this.processQueue.isEmpty());
    }

    @Override
    protected Process nextProcessToSchedule() {
        return ((PriorityQueue<Process>) processQueue).poll();
    }

    @Override
    protected void handleProcessSchedule(org.pampasim.events.Process.Schedule event) {
        this.processQueue.add(event.getProcess());
    }
}
