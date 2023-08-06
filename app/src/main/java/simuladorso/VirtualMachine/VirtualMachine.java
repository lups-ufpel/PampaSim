package simuladorso.VirtualMachine;

import simuladorso.Command.MainCommand.Invoker;
import simuladorso.Command.MainCommand.Message;
import simuladorso.Kernel.InterruptionTable;
import simuladorso.Kernel.Process;
import simuladorso.Kernel.State;
import simuladorso.VirtualMachine.Processor.Core;

public class VirtualMachine implements Runnable {
    private Core cores[];
    private Process[] runningList;
    private boolean running;

    public VirtualMachine(int numCores) {
        this.cores = new Core[numCores];
        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        System.out.println("Virtual Machine created with " + numCores + " cores");

        running = true;

        //run();
    }

    @Override
    public void run() {
        while (running) {
            // runningCores = scheduler.schedule();
            runningList = (Process[]) Invoker.invoke("Scheduler", new Message("schedule"));

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

    public void stop() {

    }

    public void restart() {

    }

    private void interruptionHandler(Process process) {
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
