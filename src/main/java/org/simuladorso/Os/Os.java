package org.simuladorso.Os;

import java.util.ArrayList;
import java.util.List;
import org.simuladorso.VirtualMachine.Sbyte;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;

public class Os {

    private final Mediator mediator;
    private final PidAllocator pidAllocator;
    private final MemoryManager memoryManager;
    final Integer MEMSIZE = 1024;

    public Os(Mediator mediator) {
        memoryManager = new MemoryManager(MEMSIZE);
        this.mediator = mediator;
        this.pidAllocator = new PidAllocator();
    }
    public void createProcess(Process.Type type, int burst, int priority, int arrivalInstant) {
        PidAllocator.Pid pid = pidAllocator.AssignPid();
        Process newProcess = new Process(pid, priority, burst, arrivalInstant);
        mediator.invoke(Mediator.Action.SCHEDULER_ADD_TO_QUEUE, new Object[]{newProcess});
        System.out.println("Created process state " + newProcess.getState());
    }
    private ArrayList<Sbyte> allocateMemory(int size) {
        ArrayList<Sbyte> stack = memoryManager.allocMemory(size);
        if (stack == null) {
            throw new IllegalArgumentException("Not enough memory, Process Not Created");
        }
        return stack;
    }
}
