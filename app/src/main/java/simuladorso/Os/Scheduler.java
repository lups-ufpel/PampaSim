package simuladorso.Os;

import java.util.ArrayList;

import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageBroker;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

public class Scheduler {
    private ArrayList<Process> readyList;
    private ArrayList<Process> waitingList;
    private ArrayList<Process> terminatedList;
    private ArrayList<Process> newList;
    private Process runningList[];
    private int clockCycles[];

    private int quantum = 4;

    private final MessageBroker invoker;

    public Scheduler(Kernel kernel, int numCores) {
        this.newList = kernel.getNewList();
        this.readyList = kernel.getReadyList();
        this.waitingList = kernel.getWaitingList();
        this.terminatedList = kernel.getTerminatedList();

        runningList = new Process[numCores];
        clockCycles = new int[numCores];

        this.invoker = MessageBroker.getInstance();
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
        for (int i = 0; i < newList.size(); i++) {
            newList.get(i).setState(State.READY);
            readyList.add(newList.remove(0));
            i--;
        }

        // verify if the waiting list has any process that can be moved to the ready
        for (int i = 0; i < waitingList.size(); i++) {
            if (waitingList.get(i).getState() == State.READY) {
                readyList.add(waitingList.remove(i));
                i--;
            }
        }

        for (int coreId = 0; coreId < runningList.length; coreId++) {
            // verify if the running list has any process that can be moved to the ready,
            // waiting or terminated list (usefull code to check if the VM has changed the
            // process state)
            if (runningList[coreId] != null) {
                if (runningList[coreId].getState() == State.TERMINATED) {
                    terminatedList.add(runningList[coreId]);
                    runningList[coreId] = null;
                } else if (runningList[coreId].getState() == State.WAITING) {
                    waitingList.add(runningList[coreId]);
                    runningList[coreId] = null;
                } else if (runningList[coreId].getState() == State.READY) {
                    readyList.add(runningList[coreId]);
                    runningList[coreId] = null;
                }
                
                clockCycles[coreId]++;

                if (clockCycles[coreId] >= quantum) {
                    readyToRunning(coreId);
                }

            }else{
                if (readyList.isEmpty()) {
                    continue;
                }
                readyToRunning(coreId);
            }

        }
        return runningList;
    }

    private void readyToRunning(int coreID) {
        if (readyList.isEmpty()) {

            // if the running process is not terminated, it will continue to run
            if (runningList[coreID].getState() == State.TERMINATED) {
                terminatedList.add(runningList[coreID]);
                runningList[coreID] = null;
            }

        } else {
            if (runningList[coreID] != null) {
                runningList[coreID].setState(State.READY);
                readyList.add(runningList[coreID]);
            }

            runningList[coreID] = readyList.remove(0);
            runningList[coreID].setState(State.RUNNING);
        }
        clockCycles[coreID] = 0;
    }

    public void printLists() {
        System.out.println("Ready List:");
        for (Process process : readyList) {
            System.out.println(process.getPid() + " " + process.getState());
        }
        System.out.println("Waiting List:");
        for (Process process : waitingList) {
            System.out.println(process.getPid() + " " + process.getState());
        }
        System.out.println("Running List:");
        for (int i = 0; i < runningList.length; i++) {
            if (runningList[i] != null) {
                System.out.println("pid: " + runningList[i].getPid() + " clockCycles: " + clockCycles[i] + " "
                        + runningList[i].getState());
            }
        }
        // System.out.println("New List:");
        // for (Process process : newList) {
        //     System.out.println(process.getPid() + " " + process.getState());
        // }
    }
}