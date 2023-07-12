package Processor;

import Kernel.Process;

public class Core {
    public void execute(Process process) {
        if (process == null) {
            System.out.println("No process to be executed");
            return;
        }
        System.out.println("Executing process " + process.getPid());
    }
}
