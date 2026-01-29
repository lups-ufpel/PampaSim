package org.pampasim.resources;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.utils.PidAllocator.Pid;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.fileops.*;
import org.pampasim.core.events.Event;
import org.pampasim.events.Memory.*;

import java.util.ArrayList;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Process {
    @Data
    public static class CreationData { // TODO: some module info needs to be part of the creation data later when a module is included
        private static long idCounter = 0;
        private final long creationId = idCounter++;
        private final int arrivalTick;
        private final int durationTicks;
        private final int startPriority;
    }

    @Getter
    @EqualsAndHashCode.Include
    private final CreationData creationData;

    // Every execution tick the process will access one, none, or several of its virtual pages, it will send out an event which
    // will require the memory module to check the Page Table or the TLB, it can also suspend the process if a page fault
    // ends up happening. This is important to justify the existence of a TLB in the system

    private final Logger LOGGER = LogManager.getLogger(Process.class);
    @EqualsAndHashCode.Include
    private final Pid pid;
    State state;
    private int currExecTime; // elapsed execution time
    @Setter
    int burstTime; // length of the next "turn" on the processor -- incorrect, but true for now
    @Setter
    private int priority;
    private int waitTime;
    @Setter
    private int endTime;

    private final ArrayList<ProcessModuleInfo> moduleInfo;


    public Process(Pid pid, CreationData creationData) {
        this.pid = pid;
        this.state = State.NEW;
        this.creationData = creationData;
        this.burstTime = creationData.durationTicks;
        this.currExecTime = 0;
        this.moduleInfo = new ArrayList<>();
    }
    public Process(Pid pid, CreationData creationData,  ProcessMemoryInfo.CreationData memoryCreationData, ProcessMemoryInfo.MemoryConfigData memoryConfigData) {
        this.pid = pid;
        this.state = State.NEW;
        this.creationData = creationData;
        this.burstTime = creationData.durationTicks;
        this.currExecTime = 0;
        this.moduleInfo = new ArrayList<>();
        moduleInfo.add(new ProcessMemoryInfo(this, memoryCreationData, memoryConfigData));
    }

    public int getRemainingExecutionTime() {
        return creationData.durationTicks - currExecTime;
    }

    public void forwardProcessExecution() {
        this.currExecTime +=1;
        this.burstTime -= 1;
    }

    public ArrayList<Event> scheduleFileSystemOperationEvents(){

        ArrayList<Event> eventsToSchedule = new ArrayList<>(); 

        if(getModuleInfo(ProcessFileSystemInfo.class).getOperations() != null){

            for(FileSystemOperation op : getModuleInfo(ProcessFileSystemInfo.class).getOperations()){
                if(op.execTime() == currExecTime){
                    switch(op){
                      case CreateFileOp crf:
                      eventsToSchedule.add(new org.pampasim.events.FileSystem.CreateFile(null, "a"));
                      break;
                      case DeleteFileOp df:
                      break;
                      case OpenFileOp of:
                      break;
                      case CloseFileOp clf:
                      break;
                      case ReadFileOp rf:
                      break;
                      case WriteFileOp wf:
                      break;
                      case CreateDirectoryOp cd:
                      break;
                      case DeleteDirectoryOp dd:
                      break;
                    }
                }

            }

        }

        return eventsToSchedule;
    }

    public void forwardWaitingTime() {
        this.waitTime += 1;
    }

    public boolean isFinished() {
        return getRemainingExecutionTime() <= 0;
    }

    public void setState(Process.State state) {
        this.state = state;
        LOGGER.debug("Process of PID {} transitioned to state {}", pid, state);
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
    public enum Type {

        /**
         * The resources.Process does not have memory and Register requirements
         */
        SIMPLE,

    }

    public void addModuleInfo(ProcessModuleInfo moduleInfo) {
        this.moduleInfo.add(moduleInfo);
    }

    public <T extends ProcessModuleInfo> T getModuleInfo(Class<T> moduleClass) {
        return moduleInfo.stream()
                .filter(moduleClass::isInstance)
                .map(moduleClass::cast)
                .findFirst()
                .orElse(null);
    }

}
