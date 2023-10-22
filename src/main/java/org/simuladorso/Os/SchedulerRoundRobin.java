package org.simuladorso.Os;

import org.simuladorso.Mediator.Mediator;

import java.util.List;

public class SchedulerRoundRobin extends TimeSharedScheduler{

    public SchedulerRoundRobin(int numCores, Mediator mediator, int quantum) {
        super(numCores, mediator, quantum);
    }

    @Override
    protected Process dequeue(List<Process> processQueue) {
        return processQueue.remove(0);
    }
}
