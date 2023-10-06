package simuladorso.Vm.Processor;

import Os.Process;

public class CoreSimple extends Core{

    public CoreSimple(){
        System.out.println("SimpleCore created");
    }
    public void execute(Process process) {
        System.out.println("number of instructions left: " + process.getLength());
        process.setLength(process.getLength() - 1);
        if(process.getLength() <= 0){
            process.setState(Process.State.TERMINATED);
            System.out.println("Process " + process.getPid() + " terminated");
            return;
        }
        if (process == null) {
            System.out.println("No process to be executed");
            return;
            }
            System.out.println("Executing process " + process.getPid() + ", state: " +
            process.getState());
    }
    
}
