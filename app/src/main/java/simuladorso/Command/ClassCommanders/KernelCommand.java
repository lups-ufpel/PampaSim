package simuladorso.Command.ClassCommanders;

import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Command;
import simuladorso.MessageBroker.Message;
import simuladorso.Kernel.Kernel;
import simuladorso.Utils.Errors.OutOfMemoryException;

public class KernelCommand implements Command {
    private Kernel kernel;

    public KernelCommand(Kernel kernel) {
        this.kernel = kernel;
    }

    public Object execute(Message msg) throws IllegalMethodCall, IllegalClassCall, OutOfMemoryException {

        switch (msg.getAction()) {
            case "newProcess":
                kernel.newProcess();
                return null;
            case "getPCB":
                return kernel.getProcess((int) msg.getParameters());
            case "getList":
                return kernel.getProcessList();
            default:
                throw new IllegalMethodCall("Kernel", msg);
        }
    }
}
