package VirtualMachine;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import Kernel.InterruptionTable;
import Kernel.ProcessLuan;
import Kernel.State;
import VirtualMachine.Processor.Core;

public class VirtualMachine {
    private Core cores[];
    private ProcessLuan[] runningList;

    public VirtualMachine(int numCores) {
        this.cores = new Core[numCores];
        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        System.out.println("Virtual Machine created with " + numCores + " cores");

        run();
    }

    private void run() {

        while (true) {

            // runningCores = scheduler.schedule();
            runningList = (ProcessLuan[]) Invoker.invoke("Scheduler", new Message("schedule"));

            for (int i = 0; i < cores.length; i++) {
                // cores[i].execute(runningCores[i]);
                if (runningList[i] != null) {
                    System.out.print("Core " + i + ": ");
                    Invoker.invoke("Core", new Message("execute", runningList[i], cores[i]));
                    if (runningList[i].hasInterruption()) {
                        interruptionHandler(runningList[i]);
                    }
                } else {
                    System.out.println("Core " + i + " is null");
                }

            }
            // teste de interrupção
            // if (runningList[2] != null)
            // runningList[2].setInterruption(InterruptionTable.KILL);
            System.out.println("====================================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void interruptionHandler(ProcessLuan process) {
        InterruptionTable interruption = (InterruptionTable) Invoker.invoke("Process",
                new Message("getInterruption", null, process));
        int pid = (int) Invoker.invoke("Process", new Message("getPid", null, process));
        
        System.out.println("------------------------------------");
        System.out.println(pid + " -> interruption: " + interruption);
        System.out.println("------------------------------------");

        switch (interruption) {
            case EXIT:
                Invoker.invoke("Process", new Message("setState", State.TERMINATED, process));
                break;

            default:
                break;
        }

    }

}
