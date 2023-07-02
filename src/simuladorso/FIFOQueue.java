package simuladorso;

import java.util.ArrayList;

public class FIFOQueue implements SchedulerPolicy{

    private ArrayList<PCB> processQueue;

    public FIFOQueue(){
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