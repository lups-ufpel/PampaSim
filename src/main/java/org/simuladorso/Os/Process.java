package org.simuladorso.Os;

import org.simuladorso.VirtualMachine.Processor.Registers;
import org.simuladorso.VirtualMachine.Sbyte;
import java.util.ArrayList;

/**
 * This abstract class represents a process in an operating system.
 * It contains the process ID, priority, arrival time, burstTime, and state.
 * It also includes methods to get and set these attributes, as well as methods for CPU percentage, parent process ID, program counter, stack size, interruption, quantum, registers, and memory.
 * The class implements the Comparable interface to allow for comparison of processes based on priority, arrival time, and process ID.
 * The State enum represents the different states a process can be in, and the Type enum represents the different types of processes.
 */
public abstract class Process {
    
    static final int DEFAULT_PRIORITY = 1;
    static final int ARRIVAL_TIME = 0;
    final Process.State INITIAL_STATE = Process.State.NEW;
    Process.State state;
    int pid;
    int priority;
    int arrivalTime;
    int currentBustTime;
    public Process(int pid) {
        this.pid = pid;
    }
    /**
     * Returns the process ID (PID) of this process.
     *
     * @return the PID of this process
     */
    public int getPid() {
        return pid;
    }
    
    /**
     * Sets the process ID (PID) of this process.
     *
     * @param pid the PID to set
     */
    public void setPid(int pid){
        this.pid = pid;
    }

    /**
     * Returns the priority of this process.
     *
     * @return the priority of this process
     */
    public int getPriority(){
        return priority;
    }

    /**
     * Sets the priority of this process.
     *
     * @param priority the priority to set
     */
    public void setPriority(int priority){
        this.priority = priority;
    }

    /**
     * Returns the arrival time of this process.
     *
     * @return the arrival time of this process
     */
    public int getArrivalTime(){
        return arrivalTime;
    }

    /**
     * Sets the arrival time of this process.
     *
     * @param arrivalTime the arrival time to set
     */
    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    /**
     * Returns the state of this process.
     *
     * @return the state of this process
     */
    public State getState(){
        return state;
    }

    /**
     * Sets the state of this process.
     *
     * @param state the state to set
     */
    public void setState(State state){
        this.state = state;
    }

    /**
     * Returns the burstTime of this process.
     *
     * @return the burstTime of this process
     */
    public int getCurrentBustTime(){
        return currentBustTime;
    }

    /**
     * Sets the burstTime of this process.
     *
     * @param burstTime the burstTime to set
     */
    public void setCurrentBustTime(int burstTime){
        this.currentBustTime = burstTime;
    }

    public int getBurstTime(){ return currentBustTime; }

    public void forwardProcessExecution(){
        setCurrentBustTime(currentBustTime + 1);
    }

    /**
     * Returns the CPU percentage of this process.
     *
     * @return the CPU percentage of this process
     */
    public int getCpuPercentage(){
        return 0;
    }
    public void setCpuPercentage(int cpuPercentage){
        System.out.println("This process does not have a cpu percentage");
        System.out.println("You should override this method in the child class");
    }
    public int getParentPid(){
        return 0;
    }
    public void setParentPid(int parentPid){
        System.out.println("This process does not have a parent pid");
        System.out.println("You should override this method in the child class");
    }
    public void incrementPc(){
        System.out.println("This process does not have a pc");
        System.out.println("You should override this method in the child class");
    }
    public int getPc(){
        return 0;
    }
    public void setPc(int pc){
        System.out.println("This process does not have a pc");
        System.out.println("You should override this method in the child class");
    }
    public int getStackSize(){
        return 0;
    }
    public void setStackSize(int stackSize){
        System.out.println("This process does not have a stack size");
        System.out.println("You should override this method in the child class");
    }
    public Interruption getInterruption(){
        return null;
    }
    public void setInterruption(Interruption interruption){
        System.out.println("This process does not have an interruption");
        System.out.println("You should override this method in the child class");
    }
    public int getQuantum(){
        return 0;
    }
    public void setQuantum(int quantum){
        System.out.println("This process does not have a quantum");
        System.out.println("You should override this method in the child class");
    }
    public boolean hasInterrupt(){
        throw new UnsupportedOperationException("Unimplemented method 'hasInterrupt'");
    }
    public Registers getRegisters(){
        return null;
    }
    public void setRegisters(Registers registers){
        System.out.println("This process does not have registers");
        System.out.println("You should override this method in the child class");
    }
    public ArrayList<Sbyte> getMemory(){
        System.out.println("This process does not have memory");
        return null;
    }
    public void setMemory(ArrayList<Sbyte> mem){
        System.out.println("This process does not have memory");
        System.out.println("You should override this method in the child class");
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

    public enum Type{

        /**
         * The Process does not have memory and Register requirements
         */
        SIMPLE,

        /**
         * The Process has memory and Register requirements
         */
        COMPLETE,

        /**
         * The Process Simple  a priority
         */
        SIMPLE_WITH_PRIORITY
    }
}
