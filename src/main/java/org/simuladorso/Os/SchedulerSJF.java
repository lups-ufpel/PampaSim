package org.simuladorso.Os;

import java.util.List;
import org.simuladorso.Mediator.Mediator;

public class SchedulerSJF extends SpaceSharedScheduler {

    public SchedulerSJF(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    @Override
    protected Process dequeue(List<Process> processQueue) {
        if (processQueue.isEmpty()) {
            LOGGER.error("dequeue was performed in a empty queue");
            return null;
        }

        Process shortestBurstTimeProcess = processQueue.get(0);

        for (Process process : processQueue) {
            if (process.getBurstTime() < shortestBurstTimeProcess.getBurstTime()) {
                shortestBurstTimeProcess = process;
            }
        }

        processQueue.remove(shortestBurstTimeProcess);
        return shortestBurstTimeProcess;
    }
}