package org.pampasim.Os;

import java.util.List;

public class SchedulerSJF extends Scheduler {

    public SchedulerSJF(int numCores) {
        super(numCores);
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