package Command.ClassCommanders;

import Command.MainCommand.Command;
import Command.MainCommand.Message;
import ProcessManagement.Kernel;

public class KernelCommand implements Command {
    private Kernel kernel;

    public KernelCommand(Kernel kernel) {
        this.kernel = kernel;
    }

    public Object execute(Message msg) {

        switch (msg.getCall()) {
            case "newProcess":
                kernel.newProcess();
                return null;
            case "getPCB":
                return kernel.getPCB((int) msg.getParam());
            case "getList":
                return kernel.getList();
            default:
                handleUnknownMethod(msg.getCall(), msg);
                return null;
        }
    }

    private void handleUnknownMethod(String methodName, Message msg) {
        System.out.println(
                "\n\n====================================== FATAL ERROR ======================================\n*");
        System.out.println("*\tNo method \"" + methodName + "\" found on Process, verify the method name.\n*");
        System.out.println("*\tMessage content:");
        System.out.println("*\t\tSender: " + msg.getSender());
        System.out.println("*\t\tCalled method: " + msg.getCall());
        System.out.println("*\t\tParams: " + msg.getParam());
        System.out.println("*\t\tReceiver: " + msg.getReceiver());
        System.out.println(
                "*\n=========================================================================================\n");
        System.exit(1);
    }
}
