package org.simuladorso.Os;

import java.util.ArrayList;
import java.util.List;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;

public abstract class Scheduler {

    int numCores;
    List<Process> readyList;
    List<Process> waitingList;
    List<Process> terminatedList;
    List<Process> newList;
    List<Process> runningList;
    int[] clockCycles; //keep track how many clock cycles each process has already executed

    public Scheduler(int numCores,Mediator mediator){
        this.numCores = numCores;
        newList = new ArrayList<Process>();
        readyList = new ArrayList<Process>();
        waitingList = new ArrayList<Process>();
        terminatedList = new ArrayList<Process>();
        runningList = new ArrayList<Process>(numCores);
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
    public abstract List<Process> schedule();

    protected void readyToRunning(int coreID) {
        if (readyList.isEmpty()) {
            
            //if currrent process is terminated it is moved to the terminated list
            if (runningList.get(coreID).getState() == Process.State.TERMINATED) {
                terminatedList.add(runningList.get(coreID));
                runningList.set(coreID, null);
            }

        } 
        else {
            if (runningList.get(coreID) != null) {
                
                runningList.get(coreID).setState(Process.State.READY);
                //Invoker.invoke("Process", new Message("setState", Process.State.READY, runningList[coreID]));
                readyList.add(runningList.get(coreID));
            }

            runningList.set(coreID, readyList.remove(0));
            runningList.get(coreID).setState(Process.State.RUNNING);
        }
        clockCycles[coreID] = 0;
    }
    public void newToReady(Process p){
        p.setState(Process.State.READY);
        enqueue(p, readyList);
    }
    protected void enqueue(Process p, List<Process> procQueue){
        procQueue.add(p);
    }
    public void addNewProcess(Process p ){
        enqueue(p, newList);
    }
    protected Process dequeue(List<Process> processQueue){
        return processQueue.remove(0);
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
        for (int i = 0; i < runningList.size(); i++) {
            if (runningList.get(i) != null) {
                System.out.println("pid: " + runningList.get(i).getPid() + " clockCycles: " + clockCycles[i] + " "
                        + runningList.get(i).getState());
            }
        }
    }
}
