package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.events.ProcessEvent;
import org.pampasim.core.resources.Process;

import java.util.Comparator;
import java.util.PriorityQueue;

// WARN: tightly coupled with baseclass thru the processEnRoute flag
public abstract class RankingScheduler extends Scheduler {
    PriorityQueue<Process> readyList;

    public RankingScheduler(Simulation simulation) {
        super(simulation);
        readyList = new PriorityQueue<>(processRankingAlgorithm());
    }
    public abstract Comparator<Process> processRankingAlgorithm();

    @Override
    public boolean shouldRunNextTick() {
        return super.shouldRunNextTick()
                || (this.lastProcessFinished() && !this.readyList.isEmpty());
    }

    @Override
    protected Process nextProcessToSchedule() {
        return readyList.poll();
    }

    @Override
    protected void handleProcessSchedule(ProcessEvent.Schedule event) {
        readyList.add(event.getProcess());
    }
}
