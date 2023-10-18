package org.simuladorso.Os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.simuladorso.VirtualMachine.Sbyte;
import org.simuladorso.Os.Process;
public class OsSimple {

    private Scheduler scheduler;
    private MemoryManager memoryManager;
    private int memorySize;
    private int initialStackSize;
    private List <Process> processList;

    public OsSimple() {
        memorySize = 1024;
        initialStackSize = 64;
        scheduler = null;
        memoryManager = new MemoryManager(memorySize);
        processList = new ArrayList<>();
    }
    public void setScheduler(Scheduler sched) {
        this.scheduler = sched;
    }
    public Scheduler getScheduler() {
        return this.scheduler;
    }
    public MemoryManager getMemoryManager() {
        return this.memoryManager;
    }
    public void setMemoryManager(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
    }

    public Process getProcess(int index) {
        return processList.get(index);
    }
    private ArrayList<Sbyte> allocateMemory(int size) {
        ArrayList<Sbyte> stack = memoryManager.allocMemory(size);
        if (stack == null) {
            throw new IllegalArgumentException("Not enough memory, Process Not Created");
        }
        return stack;
    }
    private Process createSimpleProcess(Object[] attributes) {
        int burstTime = (int) attributes[1];
        int priority = (int) attributes[2];
        int arrivalTime = (int) attributes[3];
        
        return new ProcessSimple(processList.size(),burstTime, priority, arrivalTime);
    }
    
    private Process createCompleteProcess() {
        ArrayList<Sbyte> stack = allocateMemory(initialStackSize);
        
        ProcessLuan newProcess = new ProcessLuan(processList.size());
        newProcess.setStackSize(initialStackSize);
        newProcess.setMemory(stack);
    
        return newProcess;
    }
    public void createNewProcess(Object[] attributes){
        Process.Type type = (Process.Type) attributes[0];
        Process process;
        switch (type) {
            case SIMPLE:
                process = createSimpleProcess(attributes);
                break;
            case COMPLETE:
                process = createCompleteProcess();
                break;
            default:
                throw new IllegalArgumentException(getProcessTypeErrorMessage());
        }
        processList.add(process);
        //mediator.invoke(Mediator.Action.SCHEDULER_ADD_TO_QUEUE, new Object[]{newProcess, newList});
        //adicionar o processo no escalonamento
    }
    public String getProcessTypeErrorMessage(){
        final StringBuilder message = new StringBuilder("Invalid process type. Valid types are: ");
        Arrays.stream(Process.Type.values()).forEach((Process.Type t) -> message.append(t.name() + " "));
        return message.toString();
    }



}
