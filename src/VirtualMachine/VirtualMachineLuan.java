package VirtualMachine;

import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import Kernel.InterruptionTable;
import Kernel.Process;

public class VirtualMachineLuan extends VmAbstract{

    public VirtualMachineLuan(int numCores) {
        super(numCores);
        run();
    }
    @Override
    public void run() {
         while (true) {

            // runningCores = scheduler.schedule();
            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));

            for (int i = 0; i < cores.length; i++) {
                // cores[i].execute(runningCores[i]);
                if (runningList[i] != null) {
                    System.out.print("Core " + i + ": ");
                    Invoker.invoke("Core", new Message("execute", runningList[i], cores[i]));
                    if (runningList[i].hasInterrupt()) {
                        interruptionHandler(runningList[i]);
                    }
                } else {
                    System.out.println("Core " + i + " is null");
                }

            }
            //teste de interrupção
            //if (runningList[2] != null)
            //runningList[2].setInterruption(InterruptionTable.KILL);
            System.out.println("====================================");
            try {
                 Thread.sleep(1000);
            } catch (InterruptedException e) {
                 e.printStackTrace();
            }

        }
    }

    @Override
    public void interruptionHandler(Process process) {
        InterruptionTable interruption = (InterruptionTable) Invoker.invoke("Process",
        new Message("getInterruption", null, process));
        int pid = (int) Invoker.invoke("Process", new Message("getPid", null, process));

        System.out.println("------------------------------------");
        System.out.println(pid + " -> interruption: " + interruption);
        System.out.println("------------------------------------");

        switch (interruption) {
            case EXIT:
                Invoker.invoke("Process", new Message("setState", Process.State.TERMINATED, process));
                break;

            default:
                break;
        }
    }
}
