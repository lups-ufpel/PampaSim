package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.resources.Process;

import java.util.Comparator;

@SuppressWarnings("unused")
public class SimplePriority extends RankingScheduler {
    public SimplePriority(Simulation simulation) {
        super(simulation);
    }

    @Override
    public String shortDescription() {
        return "simple single queue priority";
    }

    @Override
    public String longDescription() {
        return "Scheduler that always selects the highest (by integer value) priority in the queue. Not preemptive.";
    }

    @Override
    public Comparator<Process> processRankingAlgorithm() {
        return new Comparator<Process>() {
            @Override
            public int compare(Process lhs, Process rhs) {
                // smaller values come earlier (1)
                // lhs -> -
                // lhs = rhs -> 0
                // rhs -> +

                // (1) so this one breaks the lhs first pattern
                var order = rhs.getPriority() - lhs.getPriority();
                if (order == 0) { // creation id is the tiebreaker
                    return (int) (lhs.getCreationData().getCreationId() - rhs.getCreationData().getCreationId());
                }
                return order;
            }
        };
    }
}
