package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.Comparator;

public class SJF extends RankingScheduler {
    public SJF(Simulation simulation) {
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
                //int prio = rhs.getPriority() - lhs.getPriority();
                int SJF = lhs.getCreationData().getDurationTicks() - rhs.getCreationData().getDurationTicks();
                return SJF; //(prio == 0)? SJF : prio; // respect priority still
            }
        };
    }
}
