package simuladorso.Command.MainCommand;

import simuladorso.Command.ClassCommanders.CoreCommand;
import simuladorso.Command.ClassCommanders.KernelCommand;
import simuladorso.Command.ClassCommanders.ProcessCommand;
import simuladorso.Command.ClassCommanders.SbyteCommand;
import simuladorso.Command.ClassCommanders.SchedulerCommand;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Kernel.Kernel;
import simuladorso.Kernel.Process;
import simuladorso.Kernel.Scheduler;
import simuladorso.MessageBroker.Message;
import simuladorso.VirtualMachine.Sbyte;
import simuladorso.VirtualMachine.Processor.Core;

public class Invoker {
    private Kernel kernel;
    private Scheduler scheduler;
    private static KernelCommand kernelCommand;
    private static SchedulerCommand schedulerCommand;
    private static Process pcb;
    private static Invoker instance;

    private Invoker() {
        kernel = new Kernel();
        scheduler = new Scheduler(kernel.getNewList(), kernel.getReadyList(), kernel.getWaitingList(),
                kernel.getTerminatedList(), 4);
        kernelCommand = new KernelCommand(kernel);
        schedulerCommand = new SchedulerCommand(scheduler);
    }

    public static Invoker getInstance() {
        if (instance == null)
            instance = new Invoker();
        return instance;
    }

    public Object invoke(String className, Message message) throws IllegalMethodCall, IllegalClassCall {
        switch (className) {
            case "Kernel":
                return kernelCommand.execute(message);
            case "Process":
                if (message.getReceiver() instanceof Process) {
                    pcb = (Process) message.getReceiver();
                    ProcessCommand processCommand = new ProcessCommand(pcb);
                    return processCommand.execute(message);
                }
                System.out.println("Receiver is not a Process");
                throw new IllegalMethodCall(className, message);
            case "Scheduler":
                return schedulerCommand.execute(message);
            case "Core":
                if (message.getReceiver() instanceof Core) {
                    Core core = (Core) message.getReceiver();
                    CoreCommand coreCommand = new CoreCommand(core);
                    return coreCommand.execute(message);
                }
            case "Sbyte":
                if (message.getReceiver() instanceof Sbyte) {
                    Sbyte sbyte = (Sbyte) message.getReceiver();
                    SbyteCommand sbyteCommand = new SbyteCommand(sbyte);
                    return sbyteCommand.execute(message);
                }
            default:
                throw new IllegalClassCall(className);
        }
    }
}
