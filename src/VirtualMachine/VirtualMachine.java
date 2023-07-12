package VirtualMachine;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import Kernel.Process;
import Processor.Core;

public class VirtualMachine {
    private Core cores[];
    private Process runningCores[];

    public VirtualMachine(int numCores) {
        this.cores = new Core[numCores];
        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        run();
    }

    private void run() {

        while (true) {

            // runningCores = scheduler.schedule();
            runningCores = (Process[])Invoker.invoke("Scheduler", new Message("schedule"));

            for (int i = 0; i < cores.length; i++) {
                // cores[i].execute(runningCores[i]);
                Invoker.invoke("Core", new Message("execute", runningCores, cores[i]));
            }

        }
    }

}
