package org.simuladorso.Os;

import java.util.ArrayList;
import java.util.List;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.MediatorDefault;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Scheduler {

    int numCores;
    List<Process> readyList;
    List<Process> waitingList;
    List<Process> terminatedList;
    List<Process> newList;
    List<Process> runningList;
    Logger LOGGER = LoggerFactory.getLogger(getClass().getSimpleName());
    Mediator mediator;

    public Scheduler(int numCores,Mediator mediator) {
        this.numCores = numCores;
        this.mediator = mediator;
        newList = new ArrayList<Process>();
        readyList = new ArrayList<Process>();
        waitingList = new ArrayList<Process>();
        terminatedList = new ArrayList<Process>();
        runningList = new ArrayList<Process>(numCores);

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
    public void newToReady(Process p){
        p.setState(Process.State.READY);
        enqueue(p, readyList);
    }
    protected void enqueue(Process p, List<Process> procQueue){
        procQueue.add(p);
    }
    protected boolean isProcessSubmitted(Process p){
        int curr_tick = (int) mediator.invoke(Mediator.Action.GET_TIME);
        return p.getArrivalTime() <= curr_tick;
    }
    public void addNewProcess(Process p ){
        p.setState(Process.State.NEW);
        enqueue(p, newList);
    }
    protected Process dequeue(List<Process> processQueue){
        return processQueue.remove(0);
    }
}
