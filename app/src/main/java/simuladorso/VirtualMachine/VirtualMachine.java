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

import java.util.Arrays;

public class VirtualMachine implements Runnable {
    private Core cores[];
    private Process[] runningList;
    private boolean running;
    private final Logger logger = Logger.getInstance();
    private final Invoker invoker = new Invoker(logger);

    public VirtualMachine(int numCores) {

        Thread.setDefaultUncaughtExceptionHandler(VirtualMachine::handleException);

        this.cores = new Core[numCores];

        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        logger.info("Virtual Machine created with " + numCores + " cores");

        running = true;
    }

    public static void handleException(Thread t, Throwable e) {
        Logger.getInstance().error(e.getMessage());
    }

    @Override
    public void run() {
        this.start();
    }

    private void execute() {
        while (running) {
            System.out.println(Arrays.toString(runningList));
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

    public void start() {
        this.logger.info("VM: Starting vm");

        try {
            this.invoker.invoke("Kernel", new Message("newProcess"));
        } catch (IllegalMethodCall | IllegalClassCall | OutOfMemoryException e) {
            this.logger.error(e.getMessage());
        }

        this.running = true;
        this.execute();
    }

    public void stop() {
        this.logger.info("VM: Stoping vm");
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

    public Process[] getRunningList() {
        return runningList;
    }

    public void subscribeToLogger(Logger logger) {
        //logger.subscribe(this);
    }
}
