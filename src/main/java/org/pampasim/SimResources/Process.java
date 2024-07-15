package org.pampasim.SimResources;

import org.pampasim.SimCore.EventInfo;
import org.pampasim.SimCore.EventListener;

import java.util.HashSet;
import java.util.Set;

public class Process {
    private final Set<EventListener<EventInfo>> onStartListeners;
    private final Set<EventListener<EventInfo>> onFinishListeners;
    private final Set<EventListener<EventInfo>> onUpdateListeners;
    private final Set<EventListener<EventInfo>> onCreateListeners;
    private final Set<EventListener<EventInfo>> onSuspendListeners;
    private final Set<EventListener<EventInfo>> onResumeListeners;

    State state;
    final int arrivalTime;
    final int burstTime;
    private int priority;
    protected int currExecTime;
    private int execTimeSlice;
    protected String progressBar;
    public String name;
    public Process(int priority, int totalBurst, int arrivalTime) {
        this.state = State.NEW;
        this.priority = priority;
        this.burstTime = totalBurst;
        this.arrivalTime = arrivalTime;
        this.currExecTime = 0;
        this.progressBar = "";
        onStartListeners = new HashSet<>();
        onFinishListeners = new HashSet<>();
        onUpdateListeners = new HashSet<>();
        onCreateListeners = new HashSet<>();
        onSuspendListeners = new HashSet<>();
        onResumeListeners = new HashSet<>();
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority){
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
    public void submit() {
        if(getState() != State.NEW){
            throw new IllegalStateException("resources.Process must be in NEW state to be submitted");
        }
        else{
            setState(State.READY);
        }
    }
    public void dispatch() {
        if(getState() != State.READY){
            throw new IllegalStateException("resources.Process must be in READY state to be dispatched");
        }
        else{
            setState(State.RUNNING);
        }
    }
    public void interrupt() {
        if(getState() != State.RUNNING){
            throw new IllegalStateException("resources.Process must be in RUNNING state to be interrupted");
        }
        else{
            setState(State.READY);
        }
    }
    public void finish() {
        if(getState() != State.RUNNING){
            throw new IllegalStateException("resources.Process must be in RUNNING state to be finished");
        }
        else{
            setState(State.TERMINATED);
        }
    }
    public void forwardProcessExecution() {
        this.currExecTime +=1;
    }
    public Process addOnCreateListener(EventListener<EventInfo> listener) {
        this.onCreateListeners.add(listener);
        return this;
    }
    public Process addOnStartListener(EventListener<EventInfo> listener) {
        this.onStartListeners.add(listener);
        return this;
    }
    public Process addOnFinishListener(EventListener<EventInfo> listener) {
        this.onFinishListeners.add(listener);
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
