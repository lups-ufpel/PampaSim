package Os;

import java.util.List;

import Mediator.Mediator;

public class FCFSSimple extends Scheduler {

    public FCFSSimple(List<Process> newList, List<Process> readyList, List<Process> waitingList,
            List<Process> terminatedList, int numCores, Mediator mediator) {
        super(newList, readyList, waitingList, terminatedList, numCores, mediator);

    }
    @Override
    public Process[] schedule() {
        // verify if there are any NEW process that can be moved to the ready list
        for (int i = 0; i < newList.size(); i++) {
            newList.get(i).setState(Process.State.READY);
            //Invoker.invoke("Process", new Message("setState", Process.State.READY, newList.get(i)));
            readyList.add(newList.remove(0));
            i--;
        }

        // verify if the waiting list has any process that can be moved to the ready
        for (int i = 0; i < waitingList.size(); i++) {
            if (waitingList.get(i).getState() == Process.State.READY) {
                readyList.add(waitingList.remove(i));
                i--;
            }
        }

        for (int coreId = 0; coreId < runningList.length; coreId++) {
            // verify if the running list has any process that can be moved to the ready,
            // waiting or terminated list (usefull code to check if the VM has changed the
            // process state)
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
            } 
            else {
                if (readyList.isEmpty()) {
                    continue;
                }
                readyToRunning(coreId);
            }
        }
        return runningList;
    }
}
