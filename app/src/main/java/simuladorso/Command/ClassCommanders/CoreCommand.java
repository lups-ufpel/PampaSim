package simuladorso.Command.ClassCommanders;

import simuladorso.Command.Errors.IllegalMethodCall;
import simuladorso.Command.MainCommand.Command;
import simuladorso.Command.MainCommand.Message;
import simuladorso.Kernel.Process;
import simuladorso.VirtualMachine.Processor.Core;

public class CoreCommand implements Command {

    private Core core;

    public CoreCommand(Core core) {
        this.core = core;
    }

    public Object execute(Message msg) {
        switch (msg.getMethodName()) {
            case "execute":
                if (msg.getParam() == null || !(msg.getParam() instanceof Process)) {
                    throw new IllegalMethodCall("Core", msg);
                }
                core.execute((Process) msg.getParam());
                return null;
            default:
                throw new IllegalMethodCall("Core", msg);
        }
    }
}
