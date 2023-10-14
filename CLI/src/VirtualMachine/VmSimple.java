package VirtualMachine;

import VirtualMachine.Processor.CoreSimple;
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
    public Os.Process[] getRunningProcesses(){
        return (Os.Process[]) mediator.invoke(Mediator.Action.SCHEDULER_SCHEDULE);
    }
    public void executeProcess(Os.Process[] processes){
        for(int currCoreId=0; currCoreId < numCores; currCoreId++){

            if(runningList[currCoreId] != null){
                mediator.invoke(Mediator.Action.CORE_EXECUTE, new Object[]{runningList[currCoreId], cores[currCoreId]});
                if (runningList[currCoreId].hasInterrupt()) {
                    interruptionHandler(runningList[currCoreId]);
                }
            } else {
                System.out.println("Core " + currCoreId + " is idle");
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
