package simuladorso.Command.ClassCommanders;

import simuladorso.Command.Errors.IllegalMethodCall;
import simuladorso.Command.MainCommand.Command;
import simuladorso.Command.MainCommand.Message;
import simuladorso.Kernel.Kernel;

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
            default:
                throw new IllegalMethodCall("Kernel", msg);
        }
    }
}
