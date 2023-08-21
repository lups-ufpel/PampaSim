package simuladorso.Command.ClassCommanders;

import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Command;
import simuladorso.MessageBroker.Message;
import simuladorso.Kernel.Process;
import simuladorso.VirtualMachine.Processor.Core;

public class CoreCommand implements Command {

    private Core core;

    public CoreCommand(Core core) {
        this.core = core;
    }

    public Object execute(Message msg) throws IllegalMethodCall {
        switch (msg.getAction()) {
            case "execute":
                if (msg.getParameters() == null || !(msg.getParameters() instanceof Process)) {
                    throw new IllegalMethodCall(String.format("Core : %s", msg.toString()));
                }
                core.execute((Process) msg.getParameters());
                return null;
            default:
                throw new IllegalMethodCall(String.format("Core : %s", msg.toString()));
        }
    }
}
