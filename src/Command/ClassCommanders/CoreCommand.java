package Command.ClassCommanders;

import Command.Errors.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import Kernel.ProcessLuan;
import VirtualMachine.Processor.Core;

public class CoreCommand implements Command {

    private Core core;

    public CoreCommand(Core core) {
        this.core = core;
    }

    public Object execute(Message msg) {
        switch (msg.getMethodName()) {
            case "execute":
                if (msg.getParam() == null || !(msg.getParam() instanceof ProcessLuan)) {
                    throw new IllegalMethodCall("Core", msg);
                }
                core.execute((ProcessLuan) msg.getParam());
                return null;
            default:
                throw new IllegalMethodCall("Core", msg);
        }
    }
}
