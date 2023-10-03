package simuladorso.VirtualMachine;

import simuladorso.Logger.Logger;
import simuladorso.Mediator.Mediator;
import simuladorso.Mediator.MediatorAction;
import simuladorso.Os.InterruptionTable;
import simuladorso.Os.Process;
import simuladorso.VirtualMachine.Processor.Core;

public class VirtualMachine implements Runnable {
    private final Core[] cores;
    private Process[] runningList;
    private boolean running;
    private final Object pauseLock = new Object();
    private final Logger logger = Logger.getInstance();
    private Mediator mediator;

    public VirtualMachine(int numCores, Mediator mediator) {
        Thread.setDefaultUncaughtExceptionHandler(VirtualMachine::handleException);

        this.cores = new Core[numCores];

        for (int i = 0; i < numCores; i++) {
            this.cores[i] = new Core();
        }

        logger.info("Virtual Machine created with " + numCores + " cores");

        running = true;

        this.mediator = mediator;
    }

    public static void handleException(Thread t, Throwable e) {
        Logger.getInstance().error(e.getMessage());
    }

    @Override
    public void run() {
        this.start();
        this.execute();
    }

    private void execute() {
        while (true) {
            synchronized (this.pauseLock) {
                while (!this.running) {
                    try {
                        this.pauseLock.wait();
                    } catch (InterruptedException e) {
                        this.logger.error("Error while lock pauseLock");
                        return;
                    }
                }
            }

            try {
                this.runningList = (Process[]) this.mediator.invoke(MediatorAction.SCHEDULER_SCHEDULE);
            } catch (Exception e) {
                this.logger.error("Error while trying to schedule processes: " + e.getMessage());
            }

            for (int i = 0; i < this.cores.length; i++) {
                if (runningList[i] != null) {
                    Object[] param = new Object[2];
                    param[0] = this.cores[i];
                    param[1] = this.runningList[i];

                    mediator.invoke(MediatorAction.CORE_EXECUTE, param);

                    if (this.runningList[i].hasInterruption()) {
                        this.interruptionHandler(this.runningList[i]);
                    }
                } else {
                    logger.info("Core " + i + " is null");
                }
            }

            this.mediator.invoke(MediatorAction.UPDATE_CORES_INFO);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void start() {
        this.logger.info("VM: Starting vm");
        synchronized (this.pauseLock) {
            this.running = true;
            this.pauseLock.notify();
        }
    }

    public void stop() {
        this.logger.info("VM: Stoping vm");
        this.running = false;
    }

    private void interruptionHandler(Process process) {
        InterruptionTable interruption = process.getInterruption().get();
        int pid = process.getPid();

        logger.info(pid + " -> interruption: " + interruption);

        switch (interruption) {
            case EXIT:
                process.setState(Process.State.TERMINATED);
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
}
