package simuladorso.Os;

import java.util.ArrayList;
import java.util.List;

import simuladorso.Vm.Sbyte;

public class OS {
    protected Scheduler scheduler;
    protected List<Process> readyList;
    protected List<Process> waitingList;
    protected List<Process> terminatedList;
    protected List<Process> newList;
    protected List<Process> procList;
    protected MemoryManager memoryManager;
    protected static final int MEMORY_SIZE = 1024;
    protected static final int INITIAL_STACK_SIZE = 64;

    public OS() {
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
        return new ArrayList<Process>(procList);
    }

    public List<Process> getReadyList() {
        return readyList;
    }

    public List<Process> getWaitingList() {
        return waitingList;
    }

    public List<Process> getNewList() {
        return newList;
    }

    public List<Process> getTerminatedList() {
        return terminatedList;
    }
}
