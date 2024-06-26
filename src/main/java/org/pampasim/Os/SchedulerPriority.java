package org.pampasim.Os;



import java.util.List;

public class SchedulerPriority extends Scheduler {


    public SchedulerPriority(int numCores) {
        super(numCores);
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
