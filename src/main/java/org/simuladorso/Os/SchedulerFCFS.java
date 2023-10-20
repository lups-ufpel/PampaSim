package org.simuladorso.Os;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.simuladorso.Os.Process;
import org.simuladorso.Mediator.Mediator;

public class SchedulerFCFS extends SpaceSharedScheduler {

    
    public SchedulerFCFS(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    @Override
    public List<Process> schedule(){

        // Step 1: Move process to the appropriate lists
        moveFromRunningToFinishedList();
        movefromRunningToWaintingList();
        movefromWaitingToReadyList();

        // Step 2: Check if a new process can execute and move it if necessary
        if(newProcessCanExecute()){
            movefromNewToReadList();
        }

        // Step 3: Assign processes to cores
        for (int coreId = 0; coreId < numCores; coreId++){

            if(isThereReadyProcesses() && isCoreIdle(coreId)){

                assignProcessToCore(coreId);
            }
        }
        return runningList;
    }

    private boolean hasEmptySlots(List<Process> queue, int numOfSlots) {
        LOGGER.debug("hasEmptySlots ? [{}] queue size {} and num of slots {} ", numOfSlots>=queue.size(), queue.size(), numOfSlots);
        return numOfSlots >= queue.size();
    }
    private boolean isCoreEmpty(int coreId) {
        LOGGER.debug("isCoreEmpty ? [{}] core {} has this current process {}", runningList.get(coreId) == null, coreId, runningList.get(coreId));
        return runningList.get(coreId) == null;
    }
    private boolean isCoreIdle(int coreId) {
        return hasEmptySlots(runningList,coreId) || isCoreEmpty(coreId);
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
    @Override
    public void addNewProcess(Process p){
        p.setState(Process.State.READY);
        enqueue(p, newList);
    }

    @Override
    protected Process dequeue(List<Process> processQueue){
        return processQueue.remove(0);
    }

    public boolean isThereReadyProcesses(){
        LOGGER.debug("isThereReadyProcesses ? [{}] -> list = {}", !readyList.isEmpty(), Arrays.toString(readyList.toArray()));
        return !readyList.isEmpty();
    }
    public boolean newProcessCanExecute(){
        return !newList.isEmpty();
    }
    private List<Process> filterProcessesByState(List<Process> inputList, Process.State state) {
        return inputList.stream()
            .filter(process -> process.getState() == state)
            .collect(Collectors.toList());
    }
    private List<Process> filterNonNullProcesses(List<Process> inputList) {
        return inputList.stream()
            .filter(process -> process != null)
            .collect(Collectors.toList());
    }
    public void movefromNewToReadList(){
        List<Process> readyList = filterProcessesByState(newList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcesFromNewList(Process.State.READY);
    }
    public void movefromWaitingToReadyList(){
        List<Process> readyList = filterProcessesByState(waitingList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcessFromWaitingList(Process.State.READY);
    }
    public void movefromRunningToWaintingList(){
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
    public void removeProcesFromNewList(Process.State state){
        newList = newList.stream()
                .filter(process -> process.getState() != state)
                .collect(Collectors.toList()); 
    }
}
