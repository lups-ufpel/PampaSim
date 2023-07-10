package Command.ClassCommanders;

import Command.MainCommand.Command;
import Command.MainCommand.Message;
import ProcessManagement.Scheduler;

public class SchedulerCommand implements Command {
    private Scheduler scheduler;

    public SchedulerCommand(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Object execute(Message msg) {
        switch (msg.getCall()) {
            case "schedule":
                return scheduler.schedule();
            case "printLists":
                scheduler.printLists();
                return null;
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
