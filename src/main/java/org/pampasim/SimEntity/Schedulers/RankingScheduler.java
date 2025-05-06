package org.pampasim.SimEntity.Schedulers;

import org.pampasim.SimCore.EventType;
import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

// WARN: tightly coupled with baseclass thru the processEnRoute flag
public abstract class RankingScheduler extends Scheduler {
    PriorityQueue<Process> readyList;
    List<Process> terminatedList;

    public RankingScheduler(Simulation simulation) {
        super(simulation);
        readyList = new PriorityQueue<>(processRankingAlgorithm());
        terminatedList = new ArrayList<>();
    }
    public abstract Comparator<Process> processRankingAlgorithm();

    @Override
    protected Process nextProcessToSchedule() {
        return readyList.poll();
    }

    @Override
    protected void handleScheduleProcess(PampaSimEvent event) {
        readyList.add(event.getProcess());
    }
}
