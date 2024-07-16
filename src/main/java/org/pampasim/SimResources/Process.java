package org.pampasim.SimResources;

import org.pampasim.SimCore.EventInfo;
import org.pampasim.SimCore.EventListener;
import org.pampasim.SimCore.ProcessEventInfo;
import org.pampasim.Utils.PidAllocator;

import java.util.HashSet;
import java.util.Set;

public class Process {
    private final Set<EventListener<EventInfo>> onCreateListeners;
    private final Set<EventListener<EventInfo>> onDispatchListeners;
    private final Set<EventListener<EventInfo>> onFinishListeners;
    private final Set<EventListener<EventInfo>> onStartRunningListeners;
    private final Set<EventListener<EventInfo>> onUpdateListeners;
    private final Set<EventListener<EventInfo>> onSuspendListeners;
    private final Set<EventListener<EventInfo>> onResumeListeners;

    State state;
    final int arrivalTime;
    final int burstTime;
    private int priority;
    protected int currExecTime;
    private int execTimeSlice;
    protected String progressBar;
    private PidAllocator.Pid pid;
    public Process(int priority, int totalBurst, int arrivalTime) {
        this.priority = priority;
        this.burstTime = totalBurst;
        this.arrivalTime = arrivalTime;
        this.currExecTime = 0;
        this.progressBar = "";
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
    public void setPid(PidAllocator.Pid pid) {
        this.pid = pid;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }
    public int getArrivalTime(){
        return arrivalTime;
    }
    public int getCurrentTimeSlice() {
        return execTimeSlice;
    }
    public State getState(){
        return state;
    }
    public void setState(State state){
        this.state = state;
    }
    public int getBurstTime(){ return burstTime; }
    public int getCurrentExecutionTime() {
        return currExecTime;
    }
    public int getRemainingExecutionTime() {
        return burstTime - currExecTime;
    }
    public void create() {
        setState(State.NEW);
        notifyListenersOnCreate();
    }
    public void ready() {
        if(getState() != State.NEW) {
            throw new IllegalStateException("resources.Process must be in RUNNING state to be interrupted");
        }
        setState(State.READY);
        notifyListenerOnDispatch();
    }
    public void startRunning() {
        setState(State.RUNNING);
        notifyListenersOnStartRunning();
    }
    public void interrupt() {
        if(getState() != State.RUNNING){
            throw new IllegalStateException("resources.Process must be in RUNNING state to be interrupted");
        }
        setState(State.READY);
        notifyListenersOnSuspend();
    }
    public void finish() {
        if(getState() != State.RUNNING) {
            throw new IllegalStateException("resources.Process must be in RUNNING state to be finished");
        }
        else {
            setState(State.TERMINATED);
            notifyListenersOnFinish();
        }
    }
    public void forwardProcessExecution() {
        this.currExecTime +=1;
    }
    public Process addOnCreateListener(EventListener<EventInfo> listener) {
        this.onCreateListeners.add(listener);
        return this;
    }
    public Process addOnDispatchtListener(EventListener<EventInfo> listener) {
        this.onDispatchListeners.add(listener);
        return this;
    }
    public Process addOnFinishListener(EventListener<EventInfo> listener) {
        this.onFinishListeners.add(listener);
        return this;
    }
    public Process addOnStartRunningListener(EventListener<EventInfo> listener) {
        this.onStartRunningListeners.add(listener);
        return this;
    }
    public Process addOnUpdateListener(EventListener<EventInfo> listener) {
        this.onUpdateListeners.add(listener);
        return this;
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
        return currExecTime == burstTime;
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
