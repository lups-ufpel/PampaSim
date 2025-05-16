package org.pampasim.core.resources;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.EventInfo;
import org.pampasim.core.EventListener;
import org.pampasim.core.ProcessEventInfo;
import org.pampasim.core.utils.PidAllocator.Pid;
import java.util.HashSet;

@Getter
@EqualsAndHashCode
public class Process {
    /**
     * @param arrivalTick  real ticks
     * @param durationTick real ticks
     */
    public record CreationData(int arrivalTick, int durationTick, int startPriority) {};
    @Getter
    private final CreationData creationData;

    ;
    //TODO: during each execution tick, the process can access several pages at once. This is important for the TLB
    //Every execution tick the process will access one, none, or several of it's virtual pages, it will send out an event which
    // will require the memory module to check the Page Table or the TLB, it can also suspend the process if a page fault
    // ends up happening. This is important to justify the existence of a TLB in the system

    private final Pid pid;
    @Setter
    State state;
    private int currExecTime; // elapsed execution time
    @Setter
    int burstTime; // length of the next "turn" on the processor
    @Setter
    private int priority;

    public Process(Pid pid, CreationData creationData) {
        this.pid = pid;
        this.state = State.NEW;
        this.creationData = creationData;
        this.burstTime = creationData.durationTick;
        this.currExecTime = 0;
    }
    public String getPid() {
        return pid.toString();
    }

    public int getRemainingExecutionTime() {
        return creationData.arrivalTick - currExecTime;
    }

    public void forwardProcessExecution() {
        this.currExecTime +=1;
        this.burstTime -= 1;
    }

    public boolean isFinished() {
        return getRemainingExecutionTime() <= 0;
    }

    public enum State {
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
         * The resources.Process is currently waiting for an I/O operation to be completed.
         */
        WAITING,

        /**
         * The resources.Process has been terminated.
         */
        TERMINATED
    }
    public enum Type {

        /**
         * The resources.Process does not have memory and Register requirements
         */
        SIMPLE,

    }
}
