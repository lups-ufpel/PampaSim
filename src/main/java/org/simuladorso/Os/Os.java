package org.simuladorso.Os;

import java.util.ArrayList;
import org.simuladorso.VirtualMachine.Sbyte;
import org.simuladorso.Mediator.Mediator;


/**
 * The `Os` class represents an operating system that manages processes, memory allocation,
 * and process creation within a simulated environment. It interacts with a mediator to perform
 * the simulation of many real-world OS'ses sub-systems such scheduling tasks
 * and manage memory allocation between processes
 * <p>
 * Usage Example:
 * <pre>
 * Mediator mediator = new Mediator();
 * Os operatingSystem = new Os(mediator);
 * operatingSystem.createProcess(Process.Type.NORMAL, 50, 1, 0);
 * </pre>
 *
 * @version 1.0
 * @since 2023-10-31
 */
public class Os {

    /**
     * The mediator for communication with external components.
     */
    private final Mediator mediator;

    /**
     * The PID allocator for assigning process IDs.
     */
    private final PidAllocator pidAllocator;

    /**
     * The memory manager for managing memory allocation.
     */
    private final MemoryManager memoryManager;

    /**
     * The size of the available memory in the operating system.
     */
    final Integer MEMSIZE = 1024;


    /**
     * Constructs an `Os` instance with the specified mediator for communication.
     *
     * @param mediator The mediator for communication.
     */
    public Os(Mediator mediator) {
        memoryManager = new MemoryManager(MEMSIZE);
        this.mediator = mediator;
        this.pidAllocator = new PidAllocator();
    }

    /**
     * Create a process with the specified type, burst time, priority, and arrival instant.
     *
     * @param type           The type of process.
     * @param burst          The burst time of the process.
     * @param priority       The priority of the process.
     * @param arrivalInstant The arrival instant of the process.
     */
    public Process createProcess(Process.Type type, int burst, int priority, int arrivalInstant) {
        PidAllocator.Pid pid = pidAllocator.AssignPid();
        //Process newProcess = new Process(pid, priority, burst, arrivalInstant);
        Process newProcess = new Process(pid, priority,burst,arrivalInstant);
        mediator.invoke(Mediator.Action.SCHEDULER_ADD_TO_QUEUE, new Object[]{newProcess});
        System.out.println("Created process state " + newProcess.getState());
        return newProcess;
    }

    /**
     * Allocate memory for a process with the specified size.
     *
     * @param size The size of memory to allocate.
     * @return An ArrayList of Sbytes representing the allocated memory.
     * @throws IllegalArgumentException if there is not enough memory to allocate.
     */
    private ArrayList<Sbyte> allocateMemory(int size) {
        ArrayList<Sbyte> stack = memoryManager.allocMemory(size);
        if (stack == null) {
            throw new IllegalArgumentException("Not enough memory, Process Not Created");
        }
        return stack;
    }
}
