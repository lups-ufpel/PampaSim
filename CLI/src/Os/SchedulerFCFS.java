package Os;

import java.util.List;

import Mediator.Mediator;

public class SchedulerFCFS extends Scheduler {

    public SchedulerFCFS(List<Process> newList, List<Process> readyList, List<Process> waitingList,
            List<Process> terminatedList, int numCores, Mediator mediator) {
        super(newList, readyList, waitingList, terminatedList, numCores, mediator);

    }
    @Override
    public Process[] schedule() {
        // verify if there are any NEW process that can be moved to the ready list
        moveNewProcessesToReadyList();
        //verify if the waiting list has any process that can be moved to the ready
        moveWaitingProcessesToReadyList();

        for (int coreId = 0; coreId < runningList.length; coreId++) {
            Process currentProcess = runningList[coreId];
        
            if (currentProcess != null) {
                switch (currentProcess.getState()) {
                    case TERMINATED:
                        terminatedList.add(currentProcess);
                        runningList[coreId] = null;
                        break;
                    case WAITING:
                        waitingList.add(currentProcess);
                        runningList[coreId] = null;
                        break;
                    case READY:
                        readyList.add(currentProcess);
                        runningList[coreId] = null;
                        break;
                    case RUNNING:
                        if(!readyList.isEmpty()) readyToRunning(coreId);
                        break;
                    default:
                        throw new RuntimeException(currentProcess.getState() + ": Process in an invalid state to schedule");
                }
            }
            else{
                if(!readyList.isEmpty()) readyToRunning(coreId);
            }
        }
        return runningList;
    }
}
