package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.Comparator;

@SuppressWarnings("unused")
public class FCFS extends RankingScheduler {
    public FCFS(Simulation simulation) {
        super(simulation);
    }

    @Override
    public String shortDescription() {
        return "first-come, first-served";
    }

    @Override
    public String longDescription() {
        return "Scheduler that follows a simple queue: the earlier you get in line, the earlier you get fulfilled. Not preemptive, simple solution for batched jobs.";
    }

    @Override
    public Comparator<Process> processRankingAlgorithm() {
        return new Comparator<Process>() {
            @Override
            public int compare(Process lhs, Process rhs) {
                // smaller values come earlier
                // lhs -> -
                // lhs = rhs -> 0
                // rhs -> +
                var order = lhs.getCreationData().arrivalTick() - rhs.getCreationData().arrivalTick();
                if(order == 0) { // creation id is the tiebreaker
                  return (int) (lhs.getCreationData().getCreationId() - rhs.getCreationData().getCreationId());
                }
                return order;
            }
        };
    }
}
