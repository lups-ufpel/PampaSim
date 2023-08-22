package simuladorso.VirtualMachine;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageBroker;
import simuladorso.MessageBroker.MessageType;
import simuladorso.Os.InterruptionTable;
import simuladorso.Os.Os;
import simuladorso.Os.Process;
import simuladorso.Os.State;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;
import simuladorso.VirtualMachine.Processor.Core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class VirtualMachine implements Runnable {
    private final Core[] cores;
    private Process[] runningList;
    private boolean running;
    private final Logger logger = Logger.getInstance();
    private final MessageBroker invoker;
    private Os os;

    public VirtualMachine(int numCores) {
        Thread.setDefaultUncaughtExceptionHandler(VirtualMachine::handleException);

        this.cores = new Core[numCores];

        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        logger.info("Virtual Machine created with " + numCores + " cores");

        running = true;

        this.invoker = MessageBroker.getInstance();
        this.os = new Os(numCores);
    }

    public static void handleException(Thread t, Throwable e) {
        Logger.getInstance().error(e.getMessage());
    }

    @Override
    public void run() {
        this.invoker.invoke(MessageType.KERNEL_NEW_PROCESS);
        this.invoker.invoke(MessageType.KERNEL_NEW_PROCESS);
        this.invoker.invoke(MessageType.KERNEL_NEW_PROCESS);
        this.invoker.invoke(MessageType.KERNEL_NEW_PROCESS);
        this.invoker.invoke(MessageType.KERNEL_NEW_PROCESS);

        this.start();
    }

    private void execute() {
        while (running) {
            runningList = (Process[]) invoker.invoke(MessageType.SCHEDULER_SCHEDULE);

            for (int i = 0; i < cores.length; i++) {
                if (runningList[i] != null) {
                    LinkedList<Object> param = new LinkedList<>();
                    param.add(cores[i]);
                    param.add(runningList[i]);

                    invoker.invoke(MessageType.CORE_EXECUTE, param);

                    if (runningList[i].hasInterruption()) {
                        interruptionHandler(runningList[i]);
                    }
                } else {
                    logger.info("Core " + i + " is null");
                }

            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void start() {
        this.logger.info("VM: Starting vm");

        if (Thread.currentThread().isInterrupted())
            Thread.currentThread().start();

        this.running = true;
        this.execute();
    }

    public void stop() {
        this.logger.info("VM: Stoping vm");
        this.running = false;

        Thread.currentThread().interrupt();
    }

    private void interruptionHandler(Process process) {
        InterruptionTable interruption = process.getInterruption().get();
        int pid = process.getPid();

        logger.info(pid + " -> interruption: " + interruption);

        /*
        System.out.println("------------------------------------");
        System.out.println(pid + " -> interruption: " + interruption);
        System.out.println("------------------------------------");
        */

        switch (interruption) {
            case EXIT:
                process.setState(State.TERMINATED);
                break;
            default:
                break;
        }

    }

    public Logger getLogger() {
        return this.logger;
    }

    public Process[] getRunningList() {
        return this.runningList;
    }

    public Core[] getCores() {
        return this.cores;
    }

    public int getNumCores() {
        return this.cores.length;
    }

    public Os getOs() {
        return os;
    }

    public void setOs(Os os) {
        this.os = os;
    }
}
