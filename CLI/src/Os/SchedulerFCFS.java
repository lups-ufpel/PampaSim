package Os;

//import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import Mediator.Mediator;

public class SchedulerFCFS extends SpaceSharedScheduler {

    
    public SchedulerFCFS(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    @Override
    public Process[] schedule(){
        moveFromRunningToFinishedList();
        movefromRunningToWaintingList();
        movefromWaitingToReadyList();
        if(newProcessCanExecute()){
            movefromNewToReadList();
        }
        if(isThereReadyProcesses()){
            for(int coreId = 0; coreId < runningList.length; coreId++){
                if(runningList[coreId] == null){
                    runningList[coreId] = dequeue(readyList);
                    runningList[coreId].setState(Process.State.RUNNING);
                }
            }
        }
        return runningList;
    }
    @Override
    protected Process dequeue(List<Process> processQueue){
        return processQueue.remove(0);
    }

    public boolean isThereReadyProcesses(){
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

        List<Process> waitingList = filterProcessesByState(Arrays.asList(runningList), Process.State.WAITING);
        this.waitingList.addAll(waitingList);
        removeProcessFromRunningList(Process.State.WAITING);
    }
    public void moveFromRunningToFinishedList() {
    List<Process> finishedList = filterProcessesByState(Arrays.asList(runningList), Process.State.TERMINATED);
    terminatedList.addAll(finishedList);
    removeProcessFromRunningList(Process.State.TERMINATED);
    }
    public void removeProcessFromRunningList(Process.State state) {
        runningList = Arrays.stream(runningList)
                .map(process -> (process.getState() == state) ? null : process)
                .toArray(Process[]::new);
    }
    public void removeProcessFromWaitingList(Process.State state) {
        waitingList = waitingList.stream()
                .map(process -> (process.getState() == state) ? null : process)
                .collect(Collectors.toList()); 
    }
    public void removeProcesFromNewList(Process.State state){
        newList = newList.stream()
                .map(process -> (process.getState() == state) ? null : process)
                .collect(Collectors.toList()); 
    }
}
