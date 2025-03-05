package org.pampasim.SimCore;

public enum EventType {
    PROCESS_ARRIVAL, // handled by Processor
    ALLOCATE_PROCESS, // handled by Memory
    READY_PROCESS, // handled by ProcessManager
    SCHEDULE_PROCESS, // handled by Scheduler
    DISPATCH_PROCESS, // handled by Memory
    RUN_PROCESS, // handled by Processor
    RUN_PROCESS_ACK, // handled by Scheduler
    RUN_PROCESS_CONTINUE, // handled by Processor
    IO_OPERATION, // handled by Memory
    PREEMPT_PROCESS, // handled by Processor
    PROCESS_EXECUTION_END, // handled by ProcessManager
    END_PROCESS, // handled by Memory
    KILL_PROCESS // handled by Simulation
}
