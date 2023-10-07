package Os;

import java.util.List;
import Mediator.Mediator;
public class SchedulerLuan extends Scheduler {
    public SchedulerLuan(List<Process> newList, List<Process> readyList, List<Process> waitingList, List<Process> terminatedList, int numCores, Mediator mediator) {
        super(newList, readyList, waitingList, terminatedList, numCores,mediator);
    }

    /**
     * This method will schedule the next process to run
     * every time it is called it will count the amount of clocks the process has
     * run,
     * when the amount of clock cycles is equal to the quantum, the process will be
     * moved to the waiting list
     * and the next process will be scheduled to run.
     * The numCores determine how many processes can run at the same time, that
     * means that
     * runningList.size() will never be greater than numCores.
     * The coreID will be equivalent to the index of the runningList and
     * clockCycles.
     *
     * @return PCB[] runningList
     */
    public Process[] schedule() {
        // verify if there are any NEW process that can be moved to the ready list
        moveNewProcessesToReadyList();

        // verify if the waiting list has any process that can be moved to the ready
        moveWaitingProcessesToReadyList();

        for (int coreId = 0; coreId < runningList.length; coreId++) {
            // verify if the running list has any process that can be moved to the ready,
            // waiting or terminated list (usefull code to check if the VM has changed the
            // process state)
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

                clockCycles[coreId]++;

                if (clockCycles[coreId] >= quantum) {
                    readyToRunning(coreId);
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
