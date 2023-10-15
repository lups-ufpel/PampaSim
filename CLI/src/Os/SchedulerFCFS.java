package Os;

//import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import Mediator.Mediator;

public class SchedulerFCFS extends SpaceSharedScheduler {

    
    public SchedulerFCFS(int numCores, Mediator mediator) {
        super(numCores, mediator);
    }
    @Override
    public List<Process> schedule(){
        moveFromRunningToFinishedList();
        movefromRunningToWaintingList();
        movefromWaitingToReadyList();
        if(newProcessCanExecute()){
            movefromNewToReadList();
        }
        for(int coreId = 0; coreId < numCores; coreId++){
            if(isThereReadyProcesses()){
                Process p = dequeue(readyList);
                p.setState(Process.State.RUNNING);
                if(coreId >= runningList.size()){
                    runningList.add(p);
                }
                else if(runningList.get(coreId) == null){
                    runningList.set(coreId, p);
                }
            }
        }   
        return runningList;
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
