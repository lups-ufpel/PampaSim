package org.pampasim.SimResources;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.SimCore.EventInfo;
import org.pampasim.SimCore.EventListener;
import org.pampasim.SimCore.ProcessEventInfo;
import org.pampasim.Utils.PidAllocator.Pid;


import java.util.HashSet;
import java.util.Set;

@Getter
public class Process {
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
    }
    public String getPid() {
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
