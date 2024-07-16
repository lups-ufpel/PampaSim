package org.pampasim.gui.ViewModelEvents;

public class ProcessStartRunningEvent {
    private final String pid;
    private final int priority;
    public ProcessStartRunningEvent(String pid, int priority) {
        this.pid = pid;
        this.priority = priority;
    }
    public String getPid() {
        return pid;
    }
    public int getPriority() {
        return priority;
    }
}
