package Kernel;

import java.util.ArrayList;

import VirtualMachine.Sbyte;
import VirtualMachine.Processor.Registers;

public interface Process extends Comparable<Process> {

    // A enum State dentro da classe Process é uma prática interessante pois facilita a compreensao
    // de que o estado é algo único do processo e não de outras classes.
    // Para definir um estado, basta usar Process.State.<ESTADO> (ex: Process.State.NEW)
    enum State {
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


    int getPid();

    void setPid(int pid);

    int getParentPid();

    void setParentPid(int parentPid);

    int getPriority();

    void setPriority(int priority);

    int getArrivalTime();

    void setArrivalTime(int arrivalTime);

    int getLength();

    void setLength(int length);

    int getCpuPercentage();

    void setCpuPercentage(int cpuPercentage);

    State getState();

    void setState(State state);

    int compareTo(Process process);
    
    Registers getRegisters();

    void setRegisters(Registers registers);

    int getPc();

    void setPc(int pc);

    int getStackSize();

    void setStackSize(int stackSize);

    void incrementPc();

    Interruption getInterruption();

    void setInterruption(Interruption interruption);

    void setQuantum(int quantum);

    int getQuantum();

    ArrayList<Sbyte> getMemory();

    void setMemory(ArrayList<Sbyte> mem);

    boolean hasInterrupt();
}