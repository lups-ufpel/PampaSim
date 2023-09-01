package Command.ClassCommanders;

import Command.Errors.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import Kernel.Kernel;

public class KernelCommand implements Command {
    private Kernel kernel;

    public KernelCommand(Kernel kernel) {
        this.kernel = kernel;
    }

    public Object execute(Message msg) {

        switch (msg.getMethodName()) {
            case "newProcess":
                kernel.newProcess();
                return null;
            case "getPCB":
                return kernel.getProcess((int) msg.getParam());
            case "getList":
                return kernel.getProcessList();
            case "getProcess":
                return kernel.getProcess((int) msg.getParam());
            default:
                throw new IllegalMethodCall("Kernel", msg);
        }
    }
}
