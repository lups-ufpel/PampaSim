package ProcessManagement;
import Processor.Registers;

public class PCB {
    private int pid;
    private int parentPid;
    private int priority;
    private int cpuPercentage;
    private int arrivalTime;
    private State state;
    private Registers registers;
    private int stackPointer;
    private int stackSize;
    private int diskAddress;
    private Interruption interruption;

    // constructor will start with default values
    public PCB(int pid) {
        this.pid = pid;
        this.parentPid = 0;
        this.priority = 0;
        this.cpuPercentage = 0;
        this.arrivalTime = 0;
        this.state = State.NEW;
        this.registers = new Registers();
        this.stackPointer = 0;
        this.stackSize = 0;
        this.diskAddress = 0;
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
    public int getStackPointer() {
        return stackPointer;
    }
    public void setStackPointer(int stackPointer) {
        this.stackPointer = stackPointer;
    }
    public int getStackSize() {
        return stackSize;
    }
    public void setStackSize(int stackSize) {
        this.stackSize = stackSize;
    }
    public int getDiskAddress() {
        return diskAddress;
    }
    public void setDiskAddress(int diskAddress) {
        this.diskAddress = diskAddress;
    }
    public Interruption getInterruption() {
        return interruption;
    }
    public void setInterruption(Interruption interruption) {
        this.interruption = interruption;
    }
    public boolean hasInterruption() {
        return this.interruption.getInterruptionTable() != InterruptionTable.NONE;
    }
}
