package VirtualMachine;

import ProcessManagement.PCB;
import ProcessManagement.Kernel;
import ProcessManagement.Scheduler;
import Processor.Core;

public class VirtualMachine {
    private Core cores[];
    private Scheduler scheduler;
    private Kernel processes;
    private PCB runningCores[];

    public VirtualMachine(int numCores) {
        this.cores = new Core[numCores];
        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }
        this.processes = new Kernel();

        scheduler = new Scheduler(processes, numCores);

        run();
    }

    private void run() {
        while (true) {

            runningCores = scheduler.schedule();

            for (int i = 0; i < cores.length; i++) {
                cores[i].execute(runningCores[i]);
            }

        }
    }

    public void setRunningProcesses(Object param) {
    }

}
