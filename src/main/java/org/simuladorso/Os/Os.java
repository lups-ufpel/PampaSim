package org.simuladorso.Os;

import java.util.ArrayList;
import java.util.List;
import org.simuladorso.VirtualMachine.Sbyte;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;

public class Os {
    protected List<Process> readyList;
    protected List<Process> waitingList;
    protected List<Process> terminatedList;
    protected List<Process> newList;
    protected List<Process> procList;
    protected MemoryManager memoryManager;
    protected Mediator mediator;
    protected final int MEMORY_SIZE = 1024;
    protected final int INITIAL_STACK_SIZE = 64;

    public Os(Mediator mediator) {
        procList = new ArrayList<Process>();
        readyList = new ArrayList<Process>();
        waitingList = new ArrayList<Process>();
        terminatedList = new ArrayList<Process>();
        newList = new ArrayList<Process>();
        memoryManager = new MemoryManager(MEMORY_SIZE);
        this.mediator = mediator;
    }

    public Process getProcess(int index) {
        return procList.get(index);
    }
    public void createNewProcess(Object[] attributes) {
        Process newProcess;
        Process.Type type = (Process.Type) attributes[0];

        switch (type) {
            case SIMPLE:
                newProcess = createSimpleProcess(attributes);
                break;
            case COMPLETE:
                newProcess = createCompleteProcess();
                break;
            default:
                throw new IllegalArgumentException("Invalid process type");
        }

        procList.add(newProcess);
        mediator.invoke(Mediator.Action.SCHEDULER_ADD_TO_QUEUE, new Object[]{newProcess});
        System.out.println("Created process state " + newProcess.getState());
    }
    
    private Process createSimpleProcess(Object[] attributes) {
        int burstTime = (int) attributes[1];
        int priority = (int) attributes[2];
        int arrivalTime = (int) attributes[3];
        int pid = procList.size() + 1;
        return new ProcessSimple(pid,burstTime, priority, arrivalTime);
    }
    
    private Process createCompleteProcess() {
        ArrayList<Sbyte> stack = allocateMemory(INITIAL_STACK_SIZE);
        
        ProcessLuan newProcess = new ProcessLuan(procList.size());
        newProcess.setStackSize(INITIAL_STACK_SIZE);
        newProcess.setMemory(stack);
    
        return newProcess;
    }
    
    private ArrayList<Sbyte> allocateMemory(int size) {
        ArrayList<Sbyte> stack = memoryManager.allocMemory(size);
        if (stack == null) {
            throw new IllegalArgumentException("Not enough memory, Process Not Created");
        }
        return stack;
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
