package org.simuladorso.Os;

import org.simuladorso.Os.Process;
public class ProcessSimple extends Process {
    
    public ProcessSimple(int pid, int burstTime, int priority, int arrivalTime) {
        super(pid);
        this.burstTime = burstTime;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
    }
    public void setState(Process.State state){
        this.state = state;
    }
    public boolean hasInterrupt() {
        return false;
    }
}
