package org.simuladorso.Os;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.simuladorso.Mediator.Mediator;

public class SchedulerSJF extends SpaceSharedScheduler {


    public SchedulerSJF(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }

    @Override
    public List<Process> schedule() {
        LOGGER.debug("{} newList size = {} ",getClass().getSimpleName(), newList.size());
        LOGGER.debug("{} readyList size = {}", getClass().getSimpleName(), readyList.size());
        // Step 1: Move process to the appropriate lists
        moveFromRunningToFinishedList();
        moveFromRunningToWaitingList();
        moveFromWaitingToReadyList();

        // Step 2: Check if a new process can execute and move it if necessary
        if(newProcessCanExecute()) {
            moveFromNewToReadList();
        }

        // Step 3: Assign processes to cores
        for (int coreId = 0; coreId < numCores; coreId++){

            if(isThereReadyProcesses() && isCoreIdle(coreId)){
                assignProcessToCore(coreId);
            }
        }



        return runningList;
    }
    @Override
    protected Process dequeue(List<Process> processQueue) {
        if (processQueue.isEmpty()) {
            LOGGER.error("dequeue was performed in a empty queue");
            return null;
        }

        Process shortestBurstTimeProcess = processQueue.get(0);

        for (Process process : processQueue) {
            if (process.getBurstTime() < shortestBurstTimeProcess.getBurstTime()) {
                shortestBurstTimeProcess = process;
            }
        }

        processQueue.remove(shortestBurstTimeProcess);
        return shortestBurstTimeProcess;
    }

    public void assignProcessToCore(int coreId){
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
    private boolean isCoreIdle(int coreId) {
        return hasEmptySlots(runningList,coreId) || isCoreEmpty(coreId);
    }
    private boolean hasEmptySlots(List<Process> queue, int numOfSlots) {
        LOGGER.debug("hasEmptySlots ? [{}] queue size {} and num of slots {} ", numOfSlots>=queue.size(), queue.size(), numOfSlots);
        return numOfSlots >= queue.size();
    }
    private boolean isCoreEmpty(int coreId) {
        LOGGER.debug("isCoreEmpty ? [{}] core {} has this current process {}", runningList.get(coreId) == null, coreId, runningList.get(coreId));
        return runningList.get(coreId) == null;
    }
    public boolean isThereReadyProcesses(){
        LOGGER.debug("isThereReadyProcesses ? [{}] -> list = {}", !readyList.isEmpty(), Arrays.toString(readyList.toArray()));
        return !readyList.isEmpty();
    }
    public void moveFromNewToReadList(){
        List<Process> readyList = filterProcessesByState(newList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcesFromNewList(Process.State.READY);
    }
    public void moveFromWaitingToReadyList(){
        List<Process> readyList = filterProcessesByState(waitingList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcessFromWaitingList(Process.State.READY);
    }
    public void moveFromRunningToWaitingList(){
        List<Process> nonNullProcessesList = filterNonNullProcesses(runningList);
        List<Process> waitingList = filterProcessesByState(nonNullProcessesList, Process.State.WAITING);
        this.waitingList.addAll(waitingList);
        removeProcessFromRunningList(Process.State.WAITING);
    }
    public void moveFromRunningToFinishedList() {
        List<Process> nonNullProcessesList = filterNonNullProcesses(runningList);
        List<Process> finishedList = filterProcessesByState(nonNullProcessesList, Process.State.TERMINATED);
        terminatedList.addAll(finishedList);
        removeProcessFromRunningList(Process.State.TERMINATED);
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
    public boolean newProcessCanExecute(){
        return !newList.isEmpty();
    }
    public void removeProcesFromNewList(Process.State state){
        newList = newList.stream()
                .filter(process -> process.getState() != state)
                .collect(Collectors.toList());
    }

}