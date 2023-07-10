package Processor;

import ProcessManagement.PCB;

public class Core {
    public void execute(PCB pcb) {
        if (pcb == null) {
            System.out.println("No process to be executed");
            return;
        }
        System.out.println("Executing process " + pcb.getPid());
    }
}
