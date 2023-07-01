import java.util.ArrayList;

public class SchedulerQueue {

    private ArrayList<PCB> processQueue;

    public SchedulerQueue(){
        this.processQueue = new ArrayList<PCB>(15);
    }
    public boolean isEmpty(){
        return processQueue.isEmpty();        
    }
    public PCB getProcess(){
        return processQueue.remove(0);
    }
    public void addProcess(PCB process){
        this.processQueue.add(process);
    }
}