package Command.ClassCommanders;

import Command.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import Processor.Core;
import Kernel.Process;

public class CoreCommand implements Command {

    private Core core;

    public Object execute(Message msg) {
        switch (msg.getMethodName()) {
            case "execute":
                if (msg.getParam() == null || !(msg.getParam() instanceof Core)) {
                    throw new IllegalMethodCall("Core", msg.getMethodName(), msg);
                }
                core.execute((Process) msg.getParam());
                return null;
            default:
                throw new IllegalMethodCall("Core", msg.getMethodName(), msg);
        }
    }
}
