package Command.ClassCommanders;

import Command.Errors.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import VirtualMachine.Processor.Core;
import Kernel.Process;

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
