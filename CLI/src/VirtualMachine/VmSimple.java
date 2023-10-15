package VirtualMachine;

import VirtualMachine.Processor.CoreSimple;
import java.util.List;
import Mediator.Mediator;

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
    //@SuppressWarnings("unchecked")
    public List<Os.Process> getRunningProcesses() {
        return (List<Os.Process>) mediator.invoke(Mediator.Action.SCHEDULER_SCHEDULE);
    }
    public void executeProcess(List<Os.Process> processes){
        Os.Process proc;
        for(int coreId =0; coreId < numCores; coreId++){
            proc = processes.get(coreId);
            if(proc != null){
                mediator.invoke(Mediator.Action.CORE_EXECUTE, new Object[]{proc, cores[coreId]});
                if (proc.hasInterrupt()) {
                    interruptionHandler(proc);
                }
            }
            else{
                System.out.println("Core " + coreId + " is idle");
            }
        }
    }
    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            runningList = getRunningProcesses();
            executeProcess(runningList);
            System.out.println("====================================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    @Override
    public void interruptionHandler() {
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
    
}
