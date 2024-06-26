package org.pampasim.Os;

import org.pampasim.VirtualMachine.Processor.Registers;
import org.pampasim.VirtualMachine.Sbyte;
import org.pampasim.gui.listeners.EventListener;
import org.pampasim.gui.listeners.ProcessEventInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Process {
    private final Set<EventListener<ProcessEventInfo>> onStartListeners;
    private final Set<EventListener<ProcessEventInfo>> onFinishListeners;
    private final Set<EventListener<ProcessEventInfo>> onUpdateListeners;
    private final Set<EventListener<ProcessEventInfo>> onCreateListeners;
    private final Set<EventListener<ProcessEventInfo>> onSuspendListeners;
    private final Set<EventListener<ProcessEventInfo>> onResumeListeners;

    Process.State state;
    final PidAllocator.Pid pid;
    final int arrivalTime;
    final int burstTime;
    private int priority;
    protected int currExecTime;
    private int execTimeSlice;
    protected String progressBar;
    public Process(PidAllocator.Pid pid, int priority, int totalBurst, int arrivalTime) {
        this.pid = pid;
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
    public int getPid() {
        return pid.getNum();
    }
    public int getPriority(){
        return priority;
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
    public String getProgressBar(){
        return progressBar;
    }
    public void submit() {
        if(getState() != State.NEW){
            throw new IllegalStateException("Process must be in NEW state to be submitted");
        }
        else{
            setState(State.READY);
            notifyListenersOnStart();
        }
    }
    public void dispatch() {
        if(getState() != State.READY){
            throw new IllegalStateException("Process must be in READY state to be dispatched");
        }
        else{
            setState(State.RUNNING);
            notifyListenersOnUpdate();
        }
    }
    public void interrupt() {
        if(getState() != State.RUNNING){
            throw new IllegalStateException("Process must be in RUNNING state to be interrupted");
        }
        else{
            setState(State.READY);
        }
    }
    public void finish() {
        if(getState() != State.RUNNING){
            throw new IllegalStateException("Process must be in RUNNING state to be finished");
        }
        else{
            setState(State.TERMINATED);
            notifyListenersOnFinish();
        }
    }
    public void forwardProcessExecution() {
        this.currExecTime +=1;
        updateProgressBar();
    }
    public Process addOnCreateListener(EventListener<ProcessEventInfo> listener) {
        this.onCreateListeners.add(listener);
        return this;
    }
    public Process addOnStartListener(EventListener<ProcessEventInfo> listener) {
        this.onStartListeners.add(listener);
        return this;
    }
    public Process addOnFinishListener(EventListener<ProcessEventInfo> listener) {
        this.onFinishListeners.add(listener);
        return this;
    }
    public Process addOnUpdateListener(EventListener<ProcessEventInfo> listener) {
        this.onUpdateListeners.add(listener);
        return this;
    }
    public Process addOnSuspendListener(EventListener<ProcessEventInfo> listener) {
        this.onSuspendListeners.add(listener);
        return this;
    }
    public Process addOnResumeListener(EventListener<ProcessEventInfo> listener) {
        this.onResumeListeners.add(listener);
        return this;
    }
    public void notifyListenersOnUpdate() {
        onUpdateListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnCreate() {
        onCreateListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnStart() {
        onStartListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener,this)));
    }
    public void notifyListenersOnFinish() {
        onFinishListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
        onFinishListeners.clear();
    }
    public void notifyListenersOnSuspend() {
        onSuspendListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public void notifyListenersOnResume() {
        onResumeListeners.forEach(listener -> listener.update(ProcessEventInfo.of(listener, this)));
    }
    public double getProgress() {
        return (double) currExecTime / burstTime;
    }
    public void incrQuantum(){
        execTimeSlice +=1;
    }
    public void resetQuantum(){
        execTimeSlice = 0;
    }
    private void updateProgressBar() {
        int totalInstructions = burstTime;
        int completedInstructions = currExecTime;

        // Computes the number of "█" e "▒" needed
        int completedBlocks = (completedInstructions * 10) / totalInstructions;
        int remainingBlocks = 10 - completedBlocks;

        StringBuilder progressBarBuilder = new StringBuilder();
        progressBarBuilder.append("█".repeat(Math.max(0, completedBlocks)));
        progressBarBuilder.append("▒".repeat(Math.max(0, remainingBlocks)));

        progressBar = progressBarBuilder.toString();
    }
    public Interruption getInterruption(){
        return null;
    }
    public boolean hasInterrupt(){
        return false;
    }
    public Registers getRegisters(){
        return null;
    }
    public ArrayList<Sbyte> getMemory() {
        System.out.println("This process does not have memory");
        return null;
    }
    public enum State {
        /**
         * The Process has been just instantiated but not assigned to CPU Core.
         */
        NEW,

        /**
         * The Process has been assigned to a Scheduler Queue to be later executed.
         */
        READY,
        /**
         * The Process is currently being executed by a CPU Core.
         */
        RUNNING,

        /**
         * The Process is currently waiting for an I/O operation to be completed.
         */
        WAITING,

        /**
         * The Process has been terminated.
         */
        TERMINATED
    }
    public enum Type {

        /**
         * The Process does not have memory and Register requirements
         */
        SIMPLE,

    }
}