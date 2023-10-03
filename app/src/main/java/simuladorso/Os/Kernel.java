package simuladorso.Os;

import java.util.ArrayList;

import simuladorso.VirtualMachine.Sbyte;

public class Kernel {

    private static final int MEMORY_SIZE = 1024;
    private static final int INITIAL_STACK_SIZE = 64;

    private ArrayList<Process> readyList;
    private ArrayList<Process> waitingList;
    private ArrayList<Process> terminatedList;
    private ArrayList<Process> newList;

    private MemoryManager memoryManager;

    // this list shall not be modified by other classes
    private ArrayList<Process> procList;
    private Scheduler scheduler;

    public Kernel() {
        procList = new ArrayList<Process>();
        readyList = new ArrayList<Process>();
        waitingList = new ArrayList<Process>();
        terminatedList = new ArrayList<Process>();
        newList = new ArrayList<Process>();

        memoryManager = new MemoryManager(MEMORY_SIZE);
    }

    public Process getProcess(int index) {
        return procList.get(index);
    }

    public void newProcess(Process.Type type) {

        Process newProcess;
        if (type == Process.Type.COMPLETE) {
            
            ArrayList<Sbyte> stack = memoryManager.allocMemory(INITIAL_STACK_SIZE);
            if (stack == null) {
                throw new IllegalArgumentException("Not enough memory, Process Not Created");
            }
            newProcess = new ProcessLuan(procList.size());
            newProcess.setStackSize(INITIAL_STACK_SIZE);
            newProcess.setMemory(stack);
        } 
        else if (type == Process.Type.SIMPLE) {
            newProcess = new ProcessSimple(procList.size());
        } 
        else {
            throw new IllegalArgumentException("Invalid process type");
        }
        procList.add(newProcess);
        newList.add(newProcess);
    }

    public int getAvailablePid() {
        return this.procList.size();
    }

    /**
     * This method will return a clone of the process list
     * 
     * @return
     */
    public ArrayList<Process> getProcessList() {
        ArrayList<Process> clonedList = new ArrayList<Process>();
        clonedList.addAll(procList);
        return clonedList;
    }

    public ArrayList<Process> getReadyList() {
        return readyList;
    }

    public ArrayList<Process> getWaitingList() {
        return waitingList;
    }

    public ArrayList<Process> getNewList() {
        return newList;
    }

    public ArrayList<Process> getTerminatedList() {
        return terminatedList;
    }
}
