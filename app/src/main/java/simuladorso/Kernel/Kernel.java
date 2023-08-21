package simuladorso.Kernel;

import java.util.ArrayList;

import simuladorso.Command.MainCommand.Invoker;
import simuladorso.MessageBroker.Message;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
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
    private final Invoker invoker;

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

    public void newProcess() throws IllegalMethodCall, IllegalClassCall {

        // allocate memory for the process
        ArrayList<Sbyte> stack = memoryManager.allocMemory(INITIAL_STACK_SIZE);

        if (stack == null) {
            logger.info("Not enough memory, process not created");
            //System.out.println("Not enough memory, process not created");
            return;
        }

        Process newProcess = new Process(procList.size());

        // newProcess.setStackSize(INITIAL_STACK_SIZE);
        invoker.invoke("Process", new Message("setStackSize", INITIAL_STACK_SIZE, newProcess));
        
        // newProcess.setMemory(stack);
        invoker.invoke("Process", new Message("setMemory", stack, newProcess));

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
