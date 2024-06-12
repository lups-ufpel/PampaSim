package org.pampasim.Os;

import org.pampasim.Mediator.Mediator;

import java.util.List;
import java.util.stream.Collectors;

public class SchedulerRoundRobin extends Scheduler{

    final int QUANTUM;
    public SchedulerRoundRobin(int numCores, Mediator mediator, int quantum) {
        super(numCores, mediator);
        QUANTUM = quantum;
    }

    @Override
    public List<Process> schedule(){

        // Step 1: Move process to the appropriate lists
        moveFromRunningToFinishedList();
        moveFromRunningToWaitingList();
        moveFromWaitingToReadyList();

        // Step 2: Check if a new process can execute and move it if necessary
        if(newProcessCanExecute()){
            moveFromNewToReadyList();
        }

        // Step 2.1 Preempt any process that has overflow its time slice

        moveFromRunningToReadyList();



        // Step 3: Assign processes to cores
        for (int coreId = 0; coreId < numCores; coreId++){

            if(isThereReadyProcesses() && isCoreIdle(coreId)){

                assignProcessToCore(coreId);
            }
        }
        return runningList;
    }
    public void moveFromRunningToReadyList(){
        List<Process> nonNullProcessesList = filterNonNullProcesses(runningList);
        List<Process> preemptedProcessesList = nonNullProcessesList.stream()
                .filter(this::preemptProcess)
                .collect(Collectors.toList());
        this.readyList.addAll(preemptedProcessesList);
        removeProcessFromRunningList(Process.State.READY);
    }
    public boolean preemptProcess(Process p){

        p.incrQuantum();

        if(p.getCurrentTimeSlice() >= QUANTUM){
            p.interrupt();
            p.resetQuantum();
            return true;
        }
        return false;
    }
}
