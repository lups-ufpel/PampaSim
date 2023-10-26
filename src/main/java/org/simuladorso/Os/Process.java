package org.simuladorso.Os;

import org.simuladorso.VirtualMachine.Processor.Registers;
import org.simuladorso.VirtualMachine.Sbyte;
import java.util.ArrayList;

public class Process {

    Process.State state;

    final PidAllocator.Pid pid;
    final int arrivalTime;
    final int totalBurst;

    int priority;
    int currentBurst;
    int currentQuantum;

    private Registers registers;
    private ArrayList<Sbyte> mem;
    private Interruption interruption;



    public ArrayList<Sbyte> getMem() {
        return mem;
    }

    public void setMem(ArrayList<Sbyte> mem) {
        this.mem = mem;
    }



    public Process(PidAllocator.Pid pid, int priority, int totalBurst, int arrivalTime) {
        this.pid = pid;
        this.priority = priority;
        this.totalBurst = totalBurst;
        this.arrivalTime = arrivalTime;
        this.currentBurst = 0;
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
    public int getCurrentQuantum() {
        return currentQuantum;
    }
    public void setCurrentQuantum(int currentQuantum) {
        this.currentQuantum = currentQuantum;
    }
    public State getState(){
        return state;
    }
    public void setState(State state){
        this.state = state;
    }
    public int getTotalBurst(){ return totalBurst; }
    public int getCurrentBurst() {
        return currentBurst;
    }

    public void forwardProcessExecution(){
        this.currentBurst +=1;
    }
    public void incrQuantum(){
        currentQuantum +=1;
    }
    public void resetQuantum(){
        currentQuantum = 0;
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
    public boolean hasInterrupt(){
        return false;
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

    public enum Type {

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
