package simuladorso.Vm;

import simuladorso.Vm.Processor.CoreSimple;

import java.util.ArrayList;
import VirtualMachine.Processor.CoreSimple;
import Mediator.Mediator;
public class VmSimple extends Vm<CoreSimple>{
    public VmSimple(int numCores, Mediator mediator) {
        super(createCores(numCores));
        this.mediator = mediator;
    }

    private static CoreSimple[] createCores(int numCores) {
        CoreSimple[] cores = new CoreSimple[numCores];
        for(int i=0; i < numCores; i++){
            cores[i] = new CoreSimple();
        }
        return cores;
    }

    @Override
    public void run() {
        ArrayList<Os.Process> processList = new ArrayList<>();
        while(true){
            runningList = (Os.Process[]) mediator.invoke(Mediator.Action.SCHEDULER_SCHEDULE);
            //runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
            //processList = (ArrayList<Os.Process>) Invoker.invoke("Kernel", new Message("getList"));
            //processList = (ArrayList<Os.Process>) Mediator.invoke(MediatorAction.KERNEL_GET_LIST);
            for(int i=0; i < cores.length; i++){
                if(runningList[i] != null){
                    System.out.println("Running process " + runningList[i].getPid() + " on core " + i);
                    //Invoker.invoke("Core", new Message("execute", runningList[i],cores[i]));
                    mediator.invoke(Mediator.Action.CORE_EXECUTE, new Object[]{runningList[i], cores[i]});
                    if (runningList[i].hasInterrupt()) {
                        interruptionHandler(runningList[i]);
                    }
                } else {
                    System.out.println("Core " + i + " is idle");
                }
            }
            for(Os.Process process : processList){
                // print state of each process
                System.out.println("Process " + process.getPid() + " is " + process.getState());
            }
            System.out.println("====================================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void interruptionHandler() {
        throw new UnsupportedOperationException("Unimplemented method 'interruptionHandler'");
    }
    
}
