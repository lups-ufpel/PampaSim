package org.simuladorso.Os;

import javafx.beans.property.*;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.VirtualMachine.Processor.Registers;
import org.simuladorso.VirtualMachine.Sbyte;
import java.util.ArrayList;

public class Process {

    Process.State state;
    final PidAllocator.Pid pid;
    final int arrivalTime;
    final int burstTime;
    private int priority;
    protected int currExecTime;
    private int execTimeSlice;
    protected String progressBar;

    // Properties for JavaFX
    StringProperty stateProperty = new SimpleStringProperty();
    IntegerProperty pidProperty = new SimpleIntegerProperty();
    IntegerProperty burstProperty = new SimpleIntegerProperty();
    IntegerProperty priorityProperty = new SimpleIntegerProperty();
    IntegerProperty arrivalProperty = new SimpleIntegerProperty();
    DoubleProperty execTimeSliceProperty = new SimpleDoubleProperty();
    public Process(PidAllocator.Pid pid, int priority, int totalBurst, int arrivalTime) {

        this.pid = pid;
        this.state = State.NEW;
        this.priority = priority;
        this.burstTime = totalBurst;
        this.arrivalTime = arrivalTime;
        this.currExecTime = 0;
        this.progressBar = "";

        stateProperty.set(state.toString());
        pidProperty.set(pid.getNum());
        burstProperty.set(totalBurst);
        priorityProperty.set(priority);
        arrivalProperty.set(arrivalTime);
        execTimeSliceProperty.set(currExecTime);
    }
    public int getPid() {
        return pid.getNum();
    }

    public int getPriority(){
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
        stateProperty.set(state.toString());
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
            Mediator.getInstance().send(this, Mediator.Action.ON_THIS_PROCESS_SUBMITTED);
        }
    }


    public void dispatch(){
        if(getState() != State.READY){
            throw new IllegalStateException("Process must be in READY state to be dispatched");
        }
        else{
            setState(State.RUNNING);
            Mediator.getInstance().send(this, Mediator.Action.ON_THIS_PROCESS_DISPATCHED);
        }
    }
    public void interrupt(){
        if(getState() != State.RUNNING){
            throw new IllegalStateException("Process must be in RUNNING state to be interrupted");
        }
        else{
            setState(State.READY);
            Mediator.getInstance().send(this, Mediator.Action.ON_THIS_PROCESS_INTERRUPTED);
        }
    }
    public void finish(){
        if(getState() != State.RUNNING){
            throw new IllegalStateException("Process must be in RUNNING state to be finished");
        }
        else{
            setState(State.TERMINATED);
            Mediator.getInstance().send(this, Mediator.Action.ON_THIS_PROCESS_FINISHED);
        }
    }
    public void forwardProcessExecution(){
        this.currExecTime +=1;
        updateProgressBar();
        execTimeSliceProperty.set(currExecTime);
        Mediator.getInstance().send(this, Mediator.Action.ON_THIS_PROCESS_EXECUTED);
    }
    public double getProgress(){
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

    public StringProperty stateProperty() {
        return stateProperty;
    }
    public IntegerProperty pidProperty() {
        return pidProperty;
    }
    public IntegerProperty burstProperty() {
        return burstProperty;
    }
    public IntegerProperty priorityProperty() {
        return priorityProperty;
    }
    public IntegerProperty arrivalProperty() {
        return arrivalProperty;
    }

    public DoubleProperty execTimeSliceProperty() {
        return execTimeSliceProperty;
    }
    public void setStateProperty(String state) {
        this.stateProperty.set(state);
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
