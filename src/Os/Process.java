package Os;

import VirtualMachine.Processor.Registers;
import VirtualMachine.Sbyte;

import java.util.ArrayList;

public abstract class Process {
    
    protected int pid;
    protected int priority;
    protected int arrivalTime;
    protected int length;
    protected Process.State state;

    // Simple as it is
    public Process(int pid) {
        this.pid = pid;
        this.priority = 0;
        this.arrivalTime = 0;
        this.state = Process.State.NEW;
    }
    // Setting length
    public Process(int pid, int length){
        this(pid);
        this.length = length;
    }

    public int getPid() {
        return pid;
    }
    
    public void setPid(int pid){
        this.pid = pid;
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

    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    public State getState(){
        return state;
    }

    public void setState(State state){
        this.state = state;
    }

    public int getLength(){
        return length;
    }

    public void setLength(int length){
        this.length = length;
    }

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

    public int compareTo(Process process) {
        int priorityComparison = Integer.compare(this.getPriority(), process.getPriority());
        
        if (priorityComparison == 0) {
            // If priorities are equal, compare by arrivalTime
            int arrivalTimeComparison = Integer.compare(this.getArrivalTime(), process.getArrivalTime());
            if( arrivalTimeComparison == 0){
                // If arrivalTimes are equal, compare by pid
                return Integer.compare(this.getPid(), process.getPid());
            }
            else{
                // If arrivalTimes are not equal, return the result of the arrivalTime comparison
                return arrivalTimeComparison;
            }
        } else {
            // If priorities are not equal, return the result of the priority comparison
            return priorityComparison;
        }
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
        COMPLETE
    }
}
