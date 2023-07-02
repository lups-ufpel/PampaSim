import java.util.ArrayList;

public class SchedulerQueue {

    private ArrayList<Process> processQueue;

    public SchedulerQueue(){
        this.processQueue = new ArrayList<Process>(15);
    }
    public boolean isEmpty(){
        return processQueue.isEmpty();        
    }
    public Process getProcess(){
        return processQueue.remove(0);
    }
    public void addProcess(Process process){
        this.processQueue.add(process);
    }
}