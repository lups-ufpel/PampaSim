package org.simuladorso.Os;

import org.simuladorso.Mediator.Mediator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class TimeSharedScheduler extends Scheduler{

    int[] clockCycles; //keep track how many clock cycles each process has already executed
    final int QUANTUM;
    public TimeSharedScheduler(int numCores, Mediator mediator, int quantum) {
        super(numCores, mediator);
        clockCycles = new int[numCores];
        QUANTUM = quantum;
    }

    @Override
    public List<Process> schedule() {
        // Step 1: Move process to the appropriate lists
        moveFromRunningToFinishedList();
        moveFromRunningToWaitingList();
        moveFromWaitingToReadyList();


        // Step 1.1 PREEMPT CURRENTLY RUNNING PROCESSES
        moveFromRunningToReadyList();

        // Step 2: Check if a new process can execute and move it if necessary
        if(newProcessCanExecute()) {
            moveFromNewToReadList();
        }

        // Step 3: Assign processes to cores
        for (int coreId = 0; coreId < numCores; coreId++) {

            if(isThereReadyProcesses() && isCoreIdle(coreId)){

                assignProcessToCore(coreId);
            }
        }
        return runningList;

    }
    protected abstract Process dequeue(List<Process> processQueue);

    public boolean newProcessCanExecute(){
        return !newList.isEmpty();
    }

    public void assignProcessToCore(int coreId) {
        Process p;
        p = dequeue(readyList);
        p.setState(Process.State.RUNNING);
        if(hasEmptySlots(runningList,coreId)){
            runningList.add(p);
        }
        else{
            runningList.set(coreId, p);
        }
        LOGGER.debug("Process of pid {} was assigned to core {}", p.getPid(), runningList.indexOf(p));
    }
    public boolean isThereReadyProcesses(){
        LOGGER.debug("isThereReadyProcesses ? [{}] -> list = {}", !readyList.isEmpty(), Arrays.toString(readyList.toArray()));
        return !readyList.isEmpty();
    }
    boolean hasEmptySlots(List<Process> queue, int numOfSlots) {
        LOGGER.debug("hasEmptySlots ? [{}] queue size {} and num of slots {} ", numOfSlots>=queue.size(), queue.size(), numOfSlots);
        return numOfSlots >= queue.size();
    }
    private boolean isCoreEmpty(int coreId) {
        LOGGER.debug("isCoreEmpty ? [{}] core {} has this current process {}", runningList.get(coreId) == null, coreId, runningList.get(coreId));
        return runningList.get(coreId) == null;
    }
    boolean isCoreIdle(int coreId) {
        return hasEmptySlots(runningList,coreId) || isCoreEmpty(coreId);
    }
    public void moveFromRunningToFinishedList() {
        List<Process> nonNullProcessesList = filterNonNullProcesses(runningList);
        List<Process> finishedList = filterProcessesByState(nonNullProcessesList, Process.State.TERMINATED);
        terminatedList.addAll(finishedList);
        removeProcessFromRunningList(Process.State.TERMINATED);
    }
    public boolean preemptProcess(Process p){

        p.incrQuantum();

        if(p.currentQuantum > QUANTUM){
            p.setState(Process.State.READY);
            p.resetQuantum();
            return true;
        }
        return false;
    }
    public void moveFromRunningToReadyList(){
        List<Process> nonNullProcessesList = filterNonNullProcesses(runningList);
        List<Process> preemptedProcessesList = nonNullProcessesList.stream()
                .filter(this::preemptProcess)
                .toList();
        this.readyList.addAll(preemptedProcessesList);
        removeProcessFromRunningList(Process.State.READY);
    }
    public void moveFromRunningToWaitingList(){
        List<Process> nonNullProcessesList = filterNonNullProcesses(runningList);
        List<Process> waitingList = filterProcessesByState(nonNullProcessesList, Process.State.WAITING);
        this.waitingList.addAll(waitingList);
        removeProcessFromRunningList(Process.State.WAITING);
    }
    public void moveFromWaitingToReadyList(){
        List<Process> readyList = filterProcessesByState(waitingList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcessFromWaitingList(Process.State.READY);
    }
    public void moveFromNewToReadList(){
        List<Process> readyList = filterProcessesByState(newList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcessFromNewList(Process.State.READY);
    }
    private List<Process> filterNonNullProcesses(List<Process> inputList) {
        return inputList.stream()
                .filter(process -> process != null)
                .collect(Collectors.toList());
    }
    private List<Process> filterProcessesByState(List<Process> inputList, Process.State state) {
        return inputList.stream()
                .filter(process -> process.getState() == state)
                .collect(Collectors.toList());
    }
    public void removeProcessFromRunningList(Process.State state) {
        runningList = runningList.stream()
                .map(process -> (process!= null && process.getState() == state) ? null : process)
                .collect(Collectors.toList());
    }
    public void removeProcessFromWaitingList(Process.State state) {
        waitingList = waitingList.stream()
                .filter(process -> process.getState() != state)
                .collect(Collectors.toList());
    }
    public void removeProcessFromNewList(Process.State state){
        newList = newList.stream()
                .filter(process -> process.getState() != state)
                .collect(Collectors.toList());
    }
}
