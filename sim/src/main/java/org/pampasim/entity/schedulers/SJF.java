package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.Comparator;

@SuppressWarnings("unused")
public class SJF extends RankingScheduler {
    public SJF(Simulation simulation) {
        super(simulation);
    }

    @Override
    public String shortDescription() {
        return "smallest job first";
    }

    @Override
    public String longDescription() {
        return "Scheduler that always selects the process that takes the least time from the queue. Not preemptive, optimal turnaround time just as long as we get the whole batch at once.";
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
                var order = lhs.getCreationData().durationTicks() - rhs.getCreationData().durationTicks();
                if(order == 0) { // creation id is the tiebreaker
                    return (int) (lhs.getCreationData().getCreationId() - rhs.getCreationData().getCreationId());
                }
                return order;
            }
        };
    }
}
