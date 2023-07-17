package Kernel;

import java.util.ArrayList;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import VirtualMachine.Sbyte;

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

    public void newProcess() {

        // allocate memory for the process
        ArrayList<Sbyte> stack = memoryManager.allocMemory(INITIAL_STACK_SIZE);

        if (stack == null) {
            System.out.println("Not enough memory, process not created");
            return;
        }

        Process newProcess = new Process(procList.size());

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
