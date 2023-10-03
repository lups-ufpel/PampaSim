package Kernel;

import VirtualMachine.Processor.Registers;

import java.util.ArrayList;

import VirtualMachine.Sbyte;
public abstract class ProcessAbstract implements Process {
    
    protected int pid;
    protected int priority;
    protected int arrivalTime;
    protected int length;
    protected Process.State state;

    // Simple as it is
    public ProcessAbstract(int pid) {
        this.pid = pid;
        this.priority = 0;
        this.arrivalTime = 0;
        this.length = 0;
        this.state = Process.State.NEW;
    }
    // Setting length
    public ProcessAbstract(int pid, int length){
        this(pid);
        this.length = length;
    }

    @Override
    public int getPid() {
        return pid;
    }
    
    @Override
    public void setPid(int pid){
        this.pid = pid;
    }

    @Override
    public int getPriority(){
        return priority;
    }

    @Override
    public void setPriority(int priority){
        this.priority = priority;
    }

    @Override
    public int getArrivalTime(){
        return arrivalTime;
    }

    @Override
    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }

    @Override
    public State getState(){
        return state;
    }

    @Override
    public void setState(State state){
        this.state = state;
    }

    @Override
    public int getLength(){
        return length;
    }

    @Override
    public void setLength(int length){
        this.length = length;
    }

    @Override
    public int getCpuPercentage(){
        return 0;
    }
    @Override
    public void setCpuPercentage(int cpuPercentage){
        System.out.println("This process does not have a cpu percentage");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public int getParentPid(){
        return 0;
    }
    @Override
    public void setParentPid(int parentPid){
        System.out.println("This process does not have a parent pid");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public void incrementPc(){
        System.out.println("This process does not have a pc");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public int getPc(){
        return 0;
    }
    @Override
    public void setPc(int pc){
        System.out.println("This process does not have a pc");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public int getStackSize(){
        return 0;
    }
    @Override
    public void setStackSize(int stackSize){
        System.out.println("This process does not have a stack size");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public Interruption getInterruption(){
        return null;
    }
    @Override
    public void setInterruption(Interruption interruption){
        System.out.println("This process does not have an interruption");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public int getQuantum(){
        return 0;
    }
    @Override
    public void setQuantum(int quantum){
        System.out.println("This process does not have a quantum");
        System.out.println("You should override this method in the child class");
    }

    @Override
    public Registers getRegisters(){
        return null;
    }
    @Override
    public void setRegisters(Registers registers){
        System.out.println("This process does not have registers");
        System.out.println("You should override this method in the child class");
    }
    @Override
    public ArrayList<Sbyte> getMemory(){
        System.out.println("This process does not have memory");
        return null;
    }
    @Override
    public void setMemory(ArrayList<Sbyte> mem){
        System.out.println("This process does not have memory");
        System.out.println("You should override this method in the child class");
    }

    @Override
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
}
