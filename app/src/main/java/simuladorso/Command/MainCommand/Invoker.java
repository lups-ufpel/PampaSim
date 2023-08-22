package simuladorso.Command.MainCommand;

import simuladorso.Command.ClassCommanders.CoreCommand;
import simuladorso.Command.ClassCommanders.KernelCommand;
import simuladorso.Command.ClassCommanders.ProcessCommand;
import simuladorso.Command.ClassCommanders.SbyteCommand;
import simuladorso.Command.ClassCommanders.SchedulerCommand;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Kernel.Kernel;
import simuladorso.Kernel.Process;
import simuladorso.Kernel.Scheduler;
import simuladorso.MessageBroker.Message;
import simuladorso.Utils.Errors.OutOfMemoryException;
import simuladorso.VirtualMachine.Sbyte;
import simuladorso.VirtualMachine.Processor.Core;

import java.util.HashMap;

public class Invoker {
    private final HashMap<String, Command> commandRoutes = new HashMap<>();
    private Kernel kernel;
    private Scheduler scheduler;
    private static KernelCommand kernelCommand;
    private static SchedulerCommand schedulerCommand;
    private static Process pcb;
    private Logger logger;

    public Invoker(Logger logger) {
        this.logger = logger;

        this.kernel = new Kernel(this);
        this.scheduler = new Scheduler(kernel, 4, this);

        this.kernelCommand = new KernelCommand(kernel);
        this.schedulerCommand = new SchedulerCommand(scheduler);

        this.commandRoutes.put("Kernel", kernelCommand);
        this.commandRoutes.put("Scheduler", schedulerCommand);
    }

    public Object invoke(String className, Message message) throws IllegalMethodCall, IllegalClassCall, OutOfMemoryException {
        logger.debug(message.toString());

        switch (className) {
            case "Kernel":
                return kernelCommand.execute(message);
            case "Process":
                if (message.getReceiver() instanceof Process) {
                    pcb = (Process) message.getReceiver();
                    ProcessCommand processCommand = new ProcessCommand(pcb);
                    return processCommand.execute(message);
                }
                this.logger.warning("Object received is not a Process");
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
