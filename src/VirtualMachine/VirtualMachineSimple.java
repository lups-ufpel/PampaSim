package VirtualMachine;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import Kernel.Process;
public class VirtualMachineSimple extends VmAbstract{

    public VirtualMachineSimple(int numCores) {
        super(numCores);
    }
    @Override
    public void run() {
        while(true){
            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));

            for(int i=0; i < cores.length; i++){
                if(runningList[i] != null){
                    System.out.println("Running process " + runningList[i].getPid() + " on core " + i);
                    Invoker.invoke("Core", new Message("execute", runningList[i],cores[i]));
                    if (runningList[i].hasInterrupt()) {
                        interruptionHandler(runningList[i]);
                    }
                } else {
                    System.out.println("Core " + i + " is idle");
                }
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
