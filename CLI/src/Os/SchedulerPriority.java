package Os;

import java.util.List;

import Mediator.Mediator;

public class SchedulerPriority extends Scheduler{

    public SchedulerPriority(List<Process> newList, List<Process> readyList, List<Process> waitingList,
            List<Process> terminatedList, int numCores, Mediator mediator) {
        super(newList, readyList, waitingList, terminatedList, numCores, mediator);
    }

    @Override
    protected void readyToRunning(int coreID){
        if (readyList.isEmpty()) {

            // if the running process is not terminated, it will continue to run
            if (runningList[coreID].getState() == Process.State.TERMINATED) {
                terminatedList.add(runningList[coreID]);
                runningList[coreID] = null;
            }

        } else {
            if (runningList[coreID] != null) {
                runningList[coreID].setState(Process.State.READY);
                //Invoker.invoke("Process", new Message("setState", Process.State.READY, runningList[coreID]));
                readyList.add(runningList[coreID]);
            }

            //ESCOLHE O PROCESSO COM MAIOR PRIORIDADE
            //lógica para escolher o processo com maior prioridade
            runningList[coreID] = readyList.get(0);
            for(Process process : readyList){
                if(process.getPriority() > runningList[coreID].getPriority()){
                    runningList[coreID] = process;
                }
            }
            readyList.remove(runningList[coreID]);
            runningList[coreID].setState(Process.State.RUNNING);
        }
        clockCycles[coreID] = 0;
    }

    @Override
    public Process[] schedule() {
        // verify if there are any NEW process that can be moved to the ready list
        moveNewProcessesToReadyList();
        //verify if the waiting list has any process that can be moved to the ready
        moveWaitingProcessesToReadyList();
        
        //modify the following code to implement the priority scheduler
        for (int coreId = 0; coreId < runningList.length; coreId++) {
            // verify if the running list has any process that can be moved to the ready, waiting or terminated list (usefull code to check if the VM has changed the process state)
            // if the process is terminated or waiting, it will be removed from the running
            if (runningList[coreId] != null) {
                if (runningList[coreId].getState() == Process.State.TERMINATED) {
                    terminatedList.add(runningList[coreId]);
                    runningList[coreId] = null;
                } else if (runningList[coreId].getState() == Process.State.WAITING) {
                    waitingList.add(runningList[coreId]);
                    runningList[coreId] = null;
                } else if (runningList[coreId].getState() == Process.State.READY) {
                    readyList.add(runningList[coreId]);
                    runningList[coreId] = null;
                }
            } else {
                if (readyList.isEmpty()) {
                    continue;
                }
                readyToRunning(coreId);
            }
        }
        return runningList;
    
    
    }
    
}
