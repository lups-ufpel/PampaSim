package org.simuladorso.Os;

import org.simuladorso.Os.Process;
public class ProcessSimple extends Process {

    final int burstTime;
    public ProcessSimple(int pid, int burstTime, int priority, int arrivalTime) {
        super(pid);
        this.burstTime = burstTime;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.currentBustTime = 0;
    }
    public void setState(Process.State state){
        this.state = state;
    }
    public boolean hasInterrupt() {
        return false;
    }

    @Override
    public int getBurstTime(){ return burstTime;}
}
