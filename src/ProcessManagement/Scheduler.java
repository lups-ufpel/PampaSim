package ProcessManagement;

import java.util.ArrayList;

public class Scheduler {
    private Kernel processes;
    private ArrayList<PCB> readyList;
    private ArrayList<PCB> waitingList;
    private PCB runningList[];
    private int clockCycles[];

    private int quantum = 3;
    // private int numberOfCores;

    public Scheduler(Kernel processes, int numCores) {
        this.processes = processes;

        readyList = new ArrayList<PCB>();
        waitingList = new ArrayList<PCB>();

        // this.numberOfCores = numberOfCores;

        runningList = new PCB[numCores];
        clockCycles = new int[numCores];
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
    public PCB[] schedule() {

        // verify if there are any NEW process that can be moved to the ready list
        for (int i = processes.getList().size() - 1; i >= 0; i--) {
            if (processes.getPCB(i).getState() == State.NEW) {
                System.out.println("Process " + processes.getPCB(i).getPid() + " is now ready");
                processes.getPCB(i).setState(State.READY);
                readyList.add(processes.getPCB(i));
            } else {
                break;
            }
        }

        // verify if the waiting list has any process that can be moved to the ready
        for (int i = 0; i < waitingList.size(); i++) {
            if (waitingList.get(i).getState() == State.READY) {
                readyList.add(waitingList.remove(i));
                i--;
            }
        }

        for (int coreID = 0; coreID < runningList.length; coreID++) {
            // verify if the running list has any process that can be moved to the ready,
            // waiting or terminated list (usefull code to check if the VM has changed the
            // process state)
            if (runningList[coreID] != null) {
                if (runningList[coreID].getState() == State.TERMINATED) {
                    runningList[coreID] = null;
                } else if (runningList[coreID].getState() == State.WAITING) {
                    waitingList.add(runningList[coreID]);
                    runningList[coreID] = null;
                } else if (runningList[coreID].getState() == State.READY) {
                    readyList.add(runningList[coreID]);
                    runningList[coreID] = null;
                }
            }

            if (runningList[coreID] == null) {
                readyToRunning(coreID);
            } else {
                clockCycles[coreID]++;
                if (clockCycles[coreID] >= quantum) {

                    readyToRunning(coreID);
                }
            }
        }
        return runningList;
    }

    private void readyToRunning(int coreID) {

        if (readyList.isEmpty()) {
            if (runningList[coreID] == null) {
                return;
            }

            if (runningList[coreID].getState() == State.TERMINATED) {
                runningList[coreID] = null;
            }
        } else {
            if (runningList[coreID] != null) {
                runningList[coreID].setState(State.WAITING);
                waitingList.add(runningList[coreID]);
            }
            runningList[coreID] = readyList.remove(0);
            runningList[coreID].setState(State.RUNNING);
        }
        clockCycles[coreID] = 0;
    }

    public void printLists() {
        System.out.println("Ready List:");
        for (PCB process : readyList) {
            System.out.println(process.getPid());
        }
        System.out.println("Waiting List:");
        for (PCB process : waitingList) {
            System.out.println(process.getPid());
        }
        System.out.println("Running List:");
        for (int i = 0; i < runningList.length; i++) {
            if (runningList[i] != null) {
                System.out.println("pid: " + runningList[i].getPid() + " clockCycles: " + clockCycles[i]);
            }
        }
    }
}
