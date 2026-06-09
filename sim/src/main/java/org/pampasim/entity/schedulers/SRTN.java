package org.pampasim.entity.schedulers;

import org.pampasim.core.Simulation;
import org.pampasim.events.Process.Schedule;
import org.pampasim.resources.Process;

import java.util.Comparator;
import java.util.PriorityQueue;

@SuppressWarnings("unused")
public class SRTN extends Scheduler {
    protected Comparator<Process> comparator;
    protected PriorityQueue<Process> prioQueue;

    public SRTN() {
        super();
        this.comparator = new Comparator<Process>() {
            @Override
            public int compare(Process lhs, Process rhs) {
                // smaller values come earlier
                // lhs -> -
                // lhs = rhs -> 0
                // rhs -> +
                var order = lhs.getRemainingExecutionTime() - rhs.getRemainingExecutionTime();
                if(order == 0) { // intra tick order is the tiebreaker
                    return lhs.getCreationData().intraTickOrder() - rhs.getCreationData().intraTickOrder();
                }
                return order;
            }
        };
        config.setFullyQualifiedClassName(getClass().getCanonicalName());
        this.prioQueue = new PriorityQueue<>(comparator);
        this.processQueue = this.prioQueue;
    }
    @Override
    public String shortDescription() {
        return "shortest remaining time next";
    }

    @Override
    public String longDescription() {
        return "Preemptive scheduler that always selects the process that takes the least time from the queue, kicking the current process out if a smaller one comes up. Good for interactive systems, but may lead to starvation!";
    }

    @Override
    protected void handleProcessSchedule(Schedule event) {
        var proc = event.getProcess();
        this.prioQueue.add(proc);
        if (lastRunProcess != null
                && comparator.compare(proc, lastRunProcess) < 0) {
            // the new one is smaller, evict the current
            scheduleToNextClock(new org.pampasim.events.Process.Preemption(this, lastRunProcess));
        }
    }

    @Override
    protected Process nextProcessToSchedule() {
        return this.prioQueue.poll();
    }
}
