package VirtualMachine;

import java.util.ArrayList;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import Kernel.Process;
import VirtualMachine.Processor.CoreSimple;
public class VirtualMachineSimple extends VmAbstract<CoreSimple>{

    public VirtualMachineSimple(int numCores) {
        super(createCores(numCores));
    }

    private static CoreSimple[] createCores(int numCores){
        CoreSimple[] cores = new CoreSimple[numCores];
        for(int i=0; i < numCores; i++){
            cores[i] = new CoreSimple();
        }
        return cores;
    }

    @Override
    public void run() {
        ArrayList<Kernel.Process> processList = new ArrayList<>();
        while(true){
            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));
            processList = (ArrayList<Kernel.Process>) Invoker.invoke("Kernel", new Message("getList"));
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
            for(Kernel.Process process : processList){
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
