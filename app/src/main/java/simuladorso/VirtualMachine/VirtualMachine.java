package simuladorso.VirtualMachine;

import simuladorso.Command.MainCommand.Invoker;
import simuladorso.Logger.LogType;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.Kernel.InterruptionTable;
import simuladorso.Kernel.Process;
import simuladorso.Kernel.State;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;
import simuladorso.VirtualMachine.Processor.Core;

public class VirtualMachine implements Runnable {
    private Core cores[];
    private Process[] runningList;
    private boolean running;
    private final Logger logger;
    private final Invoker invoker;

    public VirtualMachine(int numCores) {
        this.cores = new Core[numCores];

        this.logger = new Logger();
        this.logger.setLogLevel(LogType.DEBUG);

        this.invoker = new Invoker(this.logger);

        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        logger.info("Virtual Machine created with " + numCores + " cores");

        running = true;
    }

    @Override
    public void run() {
        this.start();
    }

    private void execute() {
        while (running) {
            //runningCores = scheduler.schedule();
            try {
                runningList = (Process[]) invoker.invoke("Scheduler", new Message("schedule"));
            } catch (IllegalClassCall | IllegalMethodCall | OutOfMemoryException e) {
                logger.warning(e.getMessage());
            }

            for (int i = 0; i < cores.length; i++) {
                // cores[i].execute(runningCores[i]);
                if (runningList[i] != null) {
                    //logger.info("Core " + i + ": ");

                    try {
                        invoker.invoke("Core", new Message("execute", runningList[i], cores[i]));
                    } catch (IllegalClassCall | IllegalMethodCall | OutOfMemoryException e) {
                        logger.warning(e.getMessage());
                    }

                    if (runningList[i].hasInterruption()) {
                        interruptionHandler(runningList[i]);
                    }
                } else {
                    logger.info("Core " + i + " is null");
                }

            }
            // teste de interrupção
            // if (runningList[2] != null)
            // runningList[2].setInterruption(InterruptionTable.KILL);
            //System.out.println("====================================");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public synchronized void start() {
        this.running = true;
        this.execute();
    }

    public synchronized void stop() {
        this.running = false;
    }

    private void interruptionHandler(Process process) {
        InterruptionTable interruption = null;
        int pid = 0;

        try {
            interruption = (InterruptionTable) invoker.invoke("Process",
                    new Message("getInterruption", null, process));
            pid = (int) invoker.invoke("Process", new Message("getPid", null, process));
        } catch (IllegalMethodCall | IllegalClassCall | OutOfMemoryException e) {
            logger.warning(e.getMessage());
        }

        logger.info(pid + " -> interruption: " + interruption);

        /*
        System.out.println("------------------------------------");
        System.out.println(pid + " -> interruption: " + interruption);
        System.out.println("------------------------------------");
        */

        switch (interruption) {
            case EXIT:
                try {
                    invoker.invoke("Process", new Message("setState", State.TERMINATED, process));
                } catch (IllegalClassCall | IllegalMethodCall | OutOfMemoryException e) {
                    logger.warning(e.getMessage());
                }
                break;

            default:
                break;
        }

    }

    public Logger getLogger() {
        return this.logger;
    }

    public void subscribeToLogger(Logger logger) {
        //logger.subscribe(this);
    }
}
