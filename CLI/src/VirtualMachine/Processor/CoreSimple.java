package VirtualMachine.Processor;

import Os.Process;

public class CoreSimple extends Core{

    public CoreSimple(){
        System.out.println("SimpleCore created");
    }
    @Override
    public void execute(Process process) {
        
        process.setburstTime(process.getburstTime() - 1);
        if(process.getburstTime() <= 0){
            process.setState(Process.State.TERMINATED);
            System.out.println("Process " + process.getPid() + " terminated");
            return;
        }
        System.out.println("Executing process " + process.getPid() + ", state: " +
            process.getState());
        System.out.println("number of instructions left to execute: " + process.getburstTime());
    }
    
}
