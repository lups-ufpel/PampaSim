package org.simuladorso.VirtualMachine;

import org.simuladorso.Os.Process;
import org.simuladorso.VirtualMachine.Processor.CoreSimple;
import org.simuladorso.Mediator.Mediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VmSimple extends Vm<CoreSimple> {


    public VmSimple(int numCores, Mediator mediator) {
        super(createCores(numCores));
        if(mediator == null || numCores <= 0){
            throw new IllegalArgumentException("Mediator cannot be null and numCores must be greater than 0");
        }
        this.mediator = mediator;
    }

    private static CoreSimple[] createCores(int numCores) {
        CoreSimple[] cores = new CoreSimple[numCores];
        for(int i=0; i < numCores; i++){
            cores[i] = new CoreSimple();
        }
        return cores;
    }
    public List<Process> getRunningProcesses() {
        List<Process> processes;
        try {
            Object result = mediator.invoke(Mediator.Action.SCHEDULER_SCHEDULE);
            processes = (List<Process>) result;
            LOGGER.debug("List of Processes to be Scheduled: {}", Arrays.toString(processes.toArray()));
            return processes;
        } catch (Exception e) {
            // Handle the error, log it, or return an empty list
            LOGGER.error("Error in method getRunningProcesses: {}", e.getMessage() );
            throw new RuntimeException();
        }
    }


    public void executeProcesses(List<Process> processes){
        Process proc;
        for(int coreId =0; coreId < numCores; coreId++){
            proc = processes.get(coreId);
            if(proc != null){
                mediator.invoke(Mediator.Action.CORE_EXECUTE, new Object[]{proc, cores[coreId]});
                if (proc.hasInterrupt()) {
                    LOGGER.debug("Process of PID {} has requested an interruption",proc.getPid());
                    interruptionHandler(proc);
                }
            }
            else{
                LOGGER.info("Core [{}] is idle",coreId);
            }
        }
    }
    @Override
    public void run() {

        while(!Thread.currentThread().isInterrupted()) {

            SIM_CLOCK.incrTick();

            LOGGER.debug("Current Tick: [{}]",SIM_CLOCK.getTick());

            runningList = getRunningProcesses();

            executeProcesses(runningList);

            System.out.println("====================================");

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if(stop()){
                break;
            }
        }
    }
    @Override
    public boolean stop(){
        return (boolean) mediator.invoke(Mediator.Action.GET_SIM_STATUS);
    }
    @Override
    public void interruptionHandler() {
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
}
