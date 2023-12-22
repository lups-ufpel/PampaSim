package org.simuladorso.Os;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.simuladorso.Mediator.Mediator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Scheduler {

    Logger LOGGER = LoggerFactory.getLogger(getClass().getSimpleName());
    Mediator mediator;
    int numCores;
    List<Process> readyList;
    List<Process> waitingList;
    public List<Process> terminatedList;
    List<Process> newList;
    List<Process> runningList;

    public Scheduler(int numCores,Mediator mediator) {
        this.numCores = numCores;
        this.mediator = mediator;
        newList = new ArrayList<>();
        readyList = new ArrayList<>();
        waitingList = new ArrayList<>();
        terminatedList = new ArrayList<>();
        runningList = new ArrayList<>(numCores);

    }
    public List<Process> schedule() {

        // Step 1: Move process to the appropriate lists
        moveFromRunningToFinishedList();
        moveFromRunningToWaitingList();
        moveFromWaitingToReadyList();

        // Step 2: Check if a new process can execute and move it if necessary
        if(newProcessCanExecute()){
            moveFromNewToReadyList();
        }

        // Step 3: Assign processes to cores
        for (int coreId = 0; coreId < numCores; coreId++){

            if(isThereReadyProcesses() && isCoreIdle(coreId)){

                assignProcessToCore(coreId);
            }
        }
        return runningList;
    }
    public void assignProcessToCore(int coreId) {
        Process p;
        p = dequeue(readyList);
        p.dispatch();
        if(hasEmptySlots(runningList,coreId)){
            runningList.add(p);
        }
        else{
            runningList.set(coreId, p);
        }
        //mediator.publish(Mediator.Action.CORE_EXECUTE);
        LOGGER.debug("Process of pid {} was assigned to core {}", p.getPid(), runningList.indexOf(p));
    }
    public boolean isThereReadyProcesses() {
        LOGGER.debug("is there ready processes ? [{}] -> list = {}", !readyList.isEmpty(), Arrays.toString(readyList.toArray()));
        return !readyList.isEmpty();
    }
    public boolean isThereNewProcesses(){
        LOGGER.debug("is there new processes ? [{}] -> list = {}",!newList.isEmpty(), Arrays.toString(readyList.toArray()));
        return !newList.isEmpty();
    }
    public boolean isThereRunningProcesses(){
        List<Process> lp = filterNonNullProcesses(runningList);
        LOGGER.debug("is there running processes ? [{}] -> list = {}", !lp.isEmpty(), Arrays.toString(readyList.toArray()));
        return !lp.isEmpty();
    }
    public boolean isThereWaitingProcesses(){
        LOGGER.debug("is there waiting Processes ? [{}] -> list = {}", !waitingList.isEmpty(), Arrays.toString(readyList.toArray()));
        return !waitingList.isEmpty();
    }
    boolean hasEmptySlots(List<Process> queue, int numOfSlots) {
        LOGGER.debug("hasEmptySlots ? [{}] queue size {} and num of slots {} ", numOfSlots>=queue.size(), queue.size(), numOfSlots);
        return numOfSlots >= queue.size();
    }
    public boolean newProcessCanExecute() {
        return !newList.isEmpty();
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
    public void moveFromNewToReadyList(){
        newList.stream()
                .filter(this::isProcessSubmitted)
                .forEach(process -> {process.setState(Process.State.READY);});
        List<Process> readyList = filterProcessesByState(newList, Process.State.READY);
        this.readyList.addAll(readyList);
        removeProcessFromNewList(Process.State.READY);
    }

    protected List<Process> filterNonNullProcesses(List<Process> inputList) {
        return inputList.stream()
                .filter(Objects::nonNull)
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
