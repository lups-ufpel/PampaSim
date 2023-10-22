package org.simuladorso.Os;


import org.simuladorso.Mediator.Mediator;

import java.util.List;

public class SchedulerPriority extends SpaceSharedScheduler {


    public SchedulerPriority(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    @Override
    protected Process dequeue(List<Process> processQueue) {
        Process processToExecute = processQueue.get(0);
        for(Process nextProcess : processQueue){
            if(nextProcess.getPriority() > processToExecute.getPriority()){
                processToExecute = nextProcess;
            }
        }
        processQueue.remove(processToExecute);
        return processToExecute;
    }
}
