package Kernel;

import java.util.ArrayList;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import VirtualMachine.Sbyte;

public class Kernel {

    private static final int MEMORY_SIZE = 1024;
    private static final int INITIAL_STACK_SIZE = 64;

    private ArrayList<ProcessLuan> readyList;
    private ArrayList<ProcessLuan> waitingList;
    private ArrayList<ProcessLuan> terminatedList;
    private ArrayList<ProcessLuan> newList;

    private MemoryManager memoryManager;

    // this list shall not be modified by other classes
    private ArrayList<ProcessLuan> procList;

    public Kernel() {
        procList = new ArrayList<ProcessLuan>();
        readyList = new ArrayList<ProcessLuan>();
        waitingList = new ArrayList<ProcessLuan>();
        terminatedList = new ArrayList<ProcessLuan>();
        newList = new ArrayList<ProcessLuan>();

        memoryManager = new MemoryManager(MEMORY_SIZE);

    }

    public ProcessLuan getProcess(int index) {
        return procList.get(index);
    }

    public void newProcess() {

        // allocate memory for the process
        ArrayList<Sbyte> stack = memoryManager.allocMemory(INITIAL_STACK_SIZE);

        if (stack == null) {
            System.out.println("Not enough memory, process not created");
            return;
        }

        ProcessLuan newProcess = new ProcessLuan(procList.size());

        // newProcess.setStackSize(INITIAL_STACK_SIZE);
        Invoker.invoke("Process", new Message("setStackSize", INITIAL_STACK_SIZE, newProcess));
        
        // newProcess.setMemory(stack);
        Invoker.invoke("Process", new Message("setMemory", stack, newProcess));

        procList.add(newProcess);
        newList.add(newProcess);
    }

    /**
     * This method will return a clone of the process list
     * 
     * @return
     */
    public ArrayList<ProcessLuan> getProcessList() {
        ArrayList<ProcessLuan> clonedList = new ArrayList<ProcessLuan>();
        clonedList.addAll(procList);
        return clonedList;
    }

    public ArrayList<ProcessLuan> getReadyList() {
        return readyList;
    }

    public ArrayList<ProcessLuan> getWaitingList() {
        return waitingList;
    }

    public ArrayList<ProcessLuan> getNewList() {
        return newList;
    }

    public ArrayList<ProcessLuan> getTerminatedList() {
        return terminatedList;
    }

}
