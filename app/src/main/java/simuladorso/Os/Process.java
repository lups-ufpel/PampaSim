package simuladorso.Os;
import java.util.ArrayList;

import simuladorso.VirtualMachine.Sbyte;
import simuladorso.VirtualMachine.Processor.Registers;

public class Process {
    private int pid;
    private int parentPid;
    private int priority;
    private int cpuPercentage;
    private int arrivalTime;
    private State state;
    private Registers registers;
    private int pc;
    private int stackSize;
    private ArrayList<Sbyte> mem;
    private Interruption interruption;

    // constructor will start with default values
    public Process(int pid) {
        this.pid = pid;
        this.parentPid = 0;
        this.priority = 0;
        this.cpuPercentage = 0;
        this.arrivalTime = 0;
        this.state = State.NEW;
        this.registers = new Registers();
        this.stackSize = 0;
        this.interruption = new Interruption();
    }

    // getters and setters of all atributes
    public int getPid() {
        return pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public int getParentPid() {
        return parentPid;
    }
    public void setParentPid(int parentPid) {
        this.parentPid = parentPid;
    }
    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        if (priority >= 0 && priority <= 5) {
            this.priority = priority;
        }
    }
    public int getCpuPercentage() {
        return cpuPercentage;
    }
    public void setCpuPercentage(int cpuPercentage) {
        if (cpuPercentage >= 0 && cpuPercentage <= 100) {
            this.cpuPercentage = cpuPercentage;
        }
    }
    public int getArrivalTime() {
        return arrivalTime;
    }
    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public Registers getRegisters() {
        return registers;
    }
    public void setRegisters(Registers registers) {
        this.registers = registers;
    }
    public int getPc() {
        return pc;
    }
    public void setPc(int pc) {
        this.pc = pc;
    }
    public void incrementPc() {
        this.pc++;
    }
    public int getStackSize() {
        return stackSize;
    }
    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }
    public ArrayList<Sbyte> getMemory() {
        return mem;
    }
    public void setMemory(ArrayList<Sbyte> mem) {
        this.mem = mem;
    }
    public Interruption getInterruption() {
        return interruption;
    }
    public boolean hasInterruption() {
        return this.interruption.get() != InterruptionTable.NONE;
    }
}
