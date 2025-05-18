package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.core.resources.Process;

import java.util.Comparator;

public class FCFS extends RankingScheduler {
    public FCFS(Simulation simulation) {
        super(simulation);
    }

    @Override
    public Comparator<Process> processRankingAlgorithm() {
        return new Comparator<Process>() {
            @Override
            public int compare(Process lhs, Process rhs) {
                // lhs < rhs -> -return
                // lhs = rhs -> 0 return
                // lhs > rhs -> +return
                // the "wrong" way around, since the highest priorities need to come first
                int prio = rhs.getPriority() - lhs.getPriority();
                int fcfs = lhs.getCreationData().getArrivalTick() - rhs.getCreationData().getArrivalTick();
                return (prio == 0)? fcfs : prio; // respect priority still
            }
        };
    }
}
