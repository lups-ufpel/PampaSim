package Command.ClassCommanders;

import Command.Errors.IllegalMethodCall;
import Command.MainCommand.Command;
import Command.MainCommand.Message;
import Kernel.Scheduler;

public class SchedulerCommand implements Command {
    private Scheduler scheduler;

    public SchedulerCommand(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Object execute(Message msg) {
        switch (msg.getMethodName()) {
            case "schedule":
                return scheduler.schedule();
            case "printLists":
                scheduler.printLists();
                return null;
            default:
                throw new IllegalMethodCall("Scheduler", msg);
        }
    }

}
