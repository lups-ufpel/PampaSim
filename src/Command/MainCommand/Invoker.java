package Command.MainCommand;

import Command.ClassCommanders.KernelCommand;
import Command.ClassCommanders.ProcessCommand;
import Command.ClassCommanders.SchedulerCommand;
import ProcessManagement.Kernel;
import ProcessManagement.PCB;
import ProcessManagement.Scheduler;

public class Invoker {
    private Kernel kernel;
    private Scheduler scheduler;

    private static KernelCommand kernelCommand;
    private static SchedulerCommand schedulerCommand;
    private static PCB pcb;

    private static Invoker instance;

    public Invoker() {
        if (instance != null) {
            return;
        }
        kernel = new Kernel();
        scheduler = new Scheduler(kernel, 4);

        kernelCommand = new KernelCommand(kernel);
        schedulerCommand = new SchedulerCommand(scheduler);

        instance = this;
    }

    public Invoker getInstance() {
        if (instance == null) {
            instance = new Invoker();
        }
        return instance;
    }

    public static Object invoke(String className, Message message) {

        if (instance == null) {
            instance = new Invoker();
        }
        switch (className) {
            case "Kernel":
                return kernelCommand.execute(message);
            case "Process":
                if (message.getReceiver() instanceof PCB) {
                    pcb = (PCB) message.getReceiver();
                    ProcessCommand processCommand = new ProcessCommand(pcb);
                    return processCommand.execute(message);
                }
                System.out.println("Receiver is not a PCB");
                return null;
            case "Scheduler":
                return schedulerCommand.execute(message);
            default:
                System.out.println("Invalid command name");
                return null;
        }
    }
}
