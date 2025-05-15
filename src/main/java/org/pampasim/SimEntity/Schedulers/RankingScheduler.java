package org.pampasim.SimEntity.Schedulers;

import org.pampasim.SimCore.Simulation;
import org.pampasim.SimCore.events.ProcessSchedule;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    protected void handleProcessSchedule(ProcessSchedule event) {
        readyList.add(event.getProcess());
    }
}
