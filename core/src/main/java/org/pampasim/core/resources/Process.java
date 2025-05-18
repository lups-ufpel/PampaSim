package org.pampasim.core.resources;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.EventInfo;
import org.pampasim.core.EventListener;
import org.pampasim.core.ProcessEventInfo;
import org.pampasim.core.utils.PidAllocator.Pid;


import java.util.HashSet;
import java.util.Set;

@Getter
public class Process {
    //TODO: during each execution tick, the process can access several pages at once. This is important for the TLB
    //Every execution tick the process will access one, none, or several of it's virtual pages, it will send out an event which
    // will require the memory module to check the Page Table or the TLB, it can also suspend the process if a page fault
    // ends up happening. This is important to justify the existence of a TLB in the system
    private final Set<EventListener<EventInfo>> onCreateListeners;
    private final Set<EventListener<EventInfo>> onDispatchListeners;
    private final Set<EventListener<EventInfo>> onFinishListeners;
    private final Set<EventListener<EventInfo>> onStartRunningListeners;
    private final Set<EventListener<EventInfo>> onUpdateListeners;
    private final Set<EventListener<EventInfo>> onSuspendListeners;
    private final Set<EventListener<EventInfo>> onResumeListeners;

    State state;
    final int arrivalTime; // when the process arrives to be allocated
    @Setter
    int burstTime; // length of the next "turn" on the processor
    int totalExecTime; // total required exec time
    int totalSize; // total number of pages it occupies
    @Setter
    int virtualAddressStart; // where the start of the virtual address range is
    @Setter
    private int priority;
    private int currExecTime; // elapsed execution time
    private final Pid pid;
    public Process(int priority, int totalExecTime, int arrivalTime, Pid pid) {
        this.state = State.NEW;
        this.priority = priority;
        this.totalExecTime = totalExecTime;
        this.burstTime = totalExecTime;
        this.arrivalTime = arrivalTime;
        this.currExecTime = 0;
        this.pid = pid;
        onDispatchListeners = new HashSet<>();
        onFinishListeners = new HashSet<>();
        onUpdateListeners = new HashSet<>();
        onCreateListeners = new HashSet<>();
        onSuspendListeners = new HashSet<>();
        onResumeListeners = new HashSet<>();
        onStartRunningListeners = new HashSet<>();
        this.totalSize = 5; //TODO: Make the user able to define how many pages the process occupies
    }
    public String getPidString() {
        return pid.toString();
    }

    public int getRemainingExecutionTime() {
        return totalExecTime - currExecTime;
    }
    public void updateStateOnExecutionEnd() {
        if (isFinished()) {
            setTerminated();
        } else {
            setReady();
        }
    }
    public void forwardProcessExecution() {
        this.currExecTime +=1;
        this.burstTime -= 1;
    }
    public void addOnCreateListener(EventListener<EventInfo> listener) {
        this.onCreateListeners.add(listener);
    }
    public void addOnDispatchListener(EventListener<EventInfo> listener) {
        this.onDispatchListeners.add(listener);
    }
    public void addOnFinishListener(EventListener<EventInfo> listener) {
        this.onFinishListeners.add(listener);
    }
    public void addOnStartRunningListener(EventListener<EventInfo> listener) {
        this.onStartRunningListeners.add(listener);
    }
    public void addOnUpdateListener(EventListener<EventInfo> listener) {
        this.onUpdateListeners.add(listener);
    }
    public Process addOnSuspendListener(EventListener<EventInfo> listener) {
        this.onSuspendListeners.add(listener);
        return this;
    }
    public Process addOnResumeListener(EventListener<EventInfo> listener) {
        this.onResumeListeners.add(listener);
        return this;
    }

    public boolean isFinished() {
        return getRemainingExecutionTime() <= 0;
    }
    public void notifyListenersOnUpdate() {
        onUpdateListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnCreate() {
        onCreateListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenerOnDispatch() {
        onDispatchListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnFinish() {
        onFinishListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
        onFinishListeners.clear();
    }
    public void notifyListenersOnStartRunning() {
        onStartRunningListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnSuspend() {
        onSuspendListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnResume() {
        onResumeListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }

    public void addOnStartListener(Object notifyGuiOnStartProcess) {
    }

    public void setReady() {
        state  = State.READY;
        notifyListenerOnDispatch();
    }
    public void setRunning() {
        state  = State.RUNNING;
        notifyListenersOnStartRunning();
    }
    public void setSuspended() {
        state = State.WAITING;
        notifyListenersOnSuspend();
    }
    public void setTerminated() {
        state = State.TERMINATED;
        notifyListenersOnFinish();
    }
    public void setScheduled() {
        state = State.SCHEDULED;
    }

    public enum State {
        /**
         * The resources.Process has been just instantiated but not assigned to CPU resources.Core.
         */
        NEW,

        /**
         * The resources.Process has been assigned to a Entities.Scheduler Queue to be later executed.
         */
        READY,
        /**
         * The resources.Process is currently being executed by a CPU resources.Core.
         */
        RUNNING,

        /**
         * The resources.Process was Scheduled and is waiting for resources (memory, for example) so it can run on the processor
         */
        SCHEDULED,

        /**
         * The resources.Process is currently waiting for an I/O operation to be completed.
         */
        WAITING,

        /**
         * The resources.Process has been terminated.
         */
        TERMINATED
    }
    public enum Type {

        /**
         * The resources.Process does not have memory and Register requirements
         */
        SIMPLE,

    }
}
