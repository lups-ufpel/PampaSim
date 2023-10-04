package VirtualMachine.Processor;

import Os.Process;

public class CoreSimple extends Core{

    public CoreSimple(){
        System.out.println("SimpleCore created");
    }
    @Override
    public void execute(Process process) {
        if (process == null) {
            System.out.println("No process to be executed");
            return;
            }
            System.out.println("Executing process " + process.getPid() + ", state: " +
            process.getState());
    }
    
}
