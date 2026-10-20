package org.pampasim.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.utils.PidAllocator;

import java.util.Map;

/**
 * A PampaSim Process.
 *
 * Implemented as an interface to employ the decorator pattern,
 * which allows for factory methods to tack on extra module state and behavior.
 */
public interface Process {
    Logger LOGGER = LogManager.getLogger(Process.class);
    /**
     * @param intraTickOrder used for ordering events dispatched in the same tick,
     *                       and to later bind Processes to their ProcessViewModels.
     */
    record CreationData(
            int arrivalTick, int durationTicks, int startPriority,
            int intraTickOrder, Map<? extends Class<?>, Object> moduleCreationData) {
    }

    enum State {
        /**
         * The resources.Process has been just instantiated but not assigned to CPU resources.Core.
         */
        NEW,

        /**
         * The resources.Process has been assigned to a Entities.Scheduler Queue to be later executed.
         */
        READY,
        /**
         * The resources.Process is currently being executed by a CPU resources.Core.
         */
        RUNNING,

        /**
         * The resources.Process was Scheduled and is waiting for resources (memory, for example) so it can run on the processor
         */
        SCHEDULED,

        /**
         * The resources.Process is waiting for its turn in the processor
         */
        WAITING,

        /**
         * The resources.Process is currently waiting for its turn to run an I/O operation
         */
        IO_WAITING,

        /**
         * The resources.Process is currently performing an IO operation
         */
        IO_RUNNING,

        /**
         * The resources.Process has been terminated.
         */
        TERMINATED
    }

    // Every execution tick the process will access one, none, or several of its virtual pages.
    // to that end it will send out an event which will require the memory module to check the
    // Page Table or the TLB, it can also suspend the process if a page fault ends up happening.
    // This is important to justify the existence of a TLB in the system

    CreationData getCreationData();
    PidAllocator.Pid getPid();
    State getState();
    void setState(State state);

    int getCurrExecTime();
    int getBurstTime();
    void setBurstTime(int burstTime);
    int getPriority();
    void setPriority(int priority);
    int getWaitTime();
    int getEndTime();
    void setEndTime(int endTime);

    int getRemainingExecutionTime();
    void forwardProcessExecution();
    void forwardWaitingTime();
    boolean isFinished();

    <T extends ProcessModuleInfo> T getModuleInfo(Class<T> moduleClass);
    void addModuleInfo(ProcessModuleInfo moduleInfo);
}
