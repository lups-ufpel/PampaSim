package Kernel;

import java.util.ArrayList;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;

public class Scheduler {
    private ArrayList<ProcessLuan> readyList;
    private ArrayList<ProcessLuan> waitingList;
    private ArrayList<ProcessLuan> terminatedList;
    private ArrayList<ProcessLuan> newList;
    private ProcessLuan runningList[];
    private int clockCycles[];

    private int quantum = 4;
    // private int numberOfCores;

    public Scheduler(ArrayList<ProcessLuan> newList, ArrayList<ProcessLuan> readyList, ArrayList<ProcessLuan> waitingList,
            ArrayList<ProcessLuan> terminatedList, int numCores) {

        this.newList = newList;
        this.readyList = readyList;
        this.waitingList = waitingList;
        this.terminatedList = terminatedList;

        // this.numberOfCores = numberOfCores;

        runningList = new ProcessLuan[numCores];
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
    public ProcessLuan[] schedule() {

        // verify if there are any NEW process that can be moved to the ready list
        for (int i = 0; i < newList.size(); i++) {

            // newList.get(i).setState(State.READY);
            Invoker.invoke("Process", new Message("setState", State.READY, newList.get(i)));

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

        for (int coreID = 0; coreID < runningList.length; coreID++) {
            // verify if the running list has any process that can be moved to the ready,
            // waiting or terminated list (usefull code to check if the VM has changed the
            // process state)
            if (runningList[coreID] != null) {
                if (runningList[coreID].getState() == State.TERMINATED) {
                    terminatedList.add(runningList[coreID]);
                    runningList[coreID] = null;
                } else if (runningList[coreID].getState() == State.WAITING) {
                    waitingList.add(runningList[coreID]);
                    runningList[coreID] = null;
                } else if (runningList[coreID].getState() == State.READY) {
                    readyList.add(runningList[coreID]);
                    runningList[coreID] = null;
                }
                
                clockCycles[coreID]++;
                if (clockCycles[coreID] >= quantum) {
                    readyToRunning(coreID);
                }
            }else{
                if (readyList.isEmpty()) {
                    continue;
                }
                readyToRunning(coreID);
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
                // runningList[coreID].setState(State.READY);
                Invoker.invoke("Process", new Message("setState", State.READY, runningList[coreID]));
                readyList.add(runningList[coreID]);
            }

            runningList[coreID] = readyList.remove(0);
            runningList[coreID].setState(State.RUNNING);
        }
        clockCycles[coreID] = 0;
    }

    public void printLists() {
        System.out.println("Ready List:");
        for (ProcessLuan process : readyList) {
            System.out.println(process.getPid() + " " + process.getState());
        }
        System.out.println("Waiting List:");
        for (ProcessLuan process : waitingList) {
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
