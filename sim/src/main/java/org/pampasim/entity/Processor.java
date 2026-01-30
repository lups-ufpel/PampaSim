package org.pampasim.entity;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.resources.Process;
import org.pampasim.resources.ProcessorCore;
import org.pampasim.resources.fileops.*;
import org.pampasim.resources.ProcessFileSystemInfo;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Processor extends AbstractSimEntity {
    private final Logger LOGGER = LogManager.getLogger(Processor.class);
    private final ProcessorCore core;
    private boolean preemption;
    @Getter
    private int busyTicks;

    public Processor(Simulation simulation, ProcessorCore core) {
        super(simulation);

        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.core = core;
        this.preemption = false;
        this.busyTicks = 0;

        // Adding the events which this entity handles
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Run.class, this);
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Dispatch.class, this);
        //simulation.getEventManager().addEventHandler(org.pampasim.events.Process.RunContinue.class, this);
        simulation.getEventManager().addEventHandler(org.pampasim.events.Process.Preemption.class, this);
    }

    @Override
    public void managedRun() {
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll()); // Order: PREEMPT_PROCESS -> RUN_PROCESS_CONTINUE -> RUN_PROCESS
        }
    }

    @Override
    public void processEvent(Event event) {
        switch (event) {
            case org.pampasim.events.Process.Dispatch e -> handleProcessDispatch(e);
            case org.pampasim.events.Process.Run e -> handleProcessRun(e);
            case org.pampasim.events.Process.Preemption e -> handleProcessPreemption(e);
            default -> throw new IllegalStateException(
                    "[Scheduler] Evento do tipo "
                    + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessDispatch(org.pampasim.events.Process.Dispatch event) {
        Process process = event.getProcess();
        //process.setState(Process.State.RUNNING);
        core.setStatus(ProcessorCore.Status.BUSY);
        LOGGER.debug("Processo recebido para execução de identificador: {}", process.getPid());
        preemption = false;
        // core.execute(process);  // should only run a tick after loading the process' memory
        getSimulation().scheduleToNextClock(new org.pampasim.events.Process.Load(this, process));
    }

    private void handleProcessRun(org.pampasim.events.Process.Run event) {
        // TODO: handle IO operation schedule
        Process process = event.getProcess();


        LOGGER.debug("Execução do processo de identificador: {}", process.getPid());

        scheduleProcessFileSystemOperations(process); // must happen before core.execute (is currExecTime sensitive)

        core.execute(process);
        busyTicks++;
        if (process.isFinished() || process.getBurstTime() <= 0 || preemption) {
            core.setStatus(ProcessorCore.Status.FREE);
            getSimulation().scheduleToNextClock(new org.pampasim.events.Process.RunPaused(this, process));
            process.setState(Process.State.WAITING);
            LOGGER.debug("Fim do turno de execução do processo de identificador: {}", process.getPid());
        } else {
            getSimulation().scheduleToNextClock(new org.pampasim.events.Process.Load(this, process));
        }
    }

    public void scheduleProcessFileSystemOperations(Process process){
        if(process.getModuleInfo(ProcessFileSystemInfo.class).getOperations() != null){
            for(FileSystemOperation op : process.getModuleInfo(ProcessFileSystemInfo.class).getOperations()){
                if(op.execTime() == process.getCurrExecTime()){
                    switch(op){
                      case CreateFileOp crf:
                      scheduleToNextClock(new org.pampasim.events.FileSystem.CreateFile(null, "a"));
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
    }
    private void handleProcessPreemption(org.pampasim.events.Process.Preemption event) {
        preemption = true;
        LOGGER.debug("Interrupção da execução de processo de identificador: {}", event.getProcess().getPid());
    }
    public boolean isFree() {
        return ProcessorCore.Status.FREE == this.core.getStatus();
    }

    private int getEventPriority(Event event) {
        return switch (event) {
            case org.pampasim.events.Process.Preemption _e -> 1;   // Highest priority
            case org.pampasim.events.Process.Run _e -> 2;
            case org.pampasim.events.Process.Dispatch _e -> 3;       // Lowest priority
            default -> Integer.MAX_VALUE;
        };
    }
}
