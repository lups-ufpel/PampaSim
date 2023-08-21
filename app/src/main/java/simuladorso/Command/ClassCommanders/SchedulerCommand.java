package simuladorso.Command.ClassCommanders;

import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Command;
import simuladorso.MessageBroker.Message;
import simuladorso.Kernel.Scheduler;
import simuladorso.Utils.Errors.OutOfMemoryException;

public class SchedulerCommand implements Command {
    private Scheduler scheduler;

    public SchedulerCommand(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Object execute(Message msg) throws IllegalMethodCall, IllegalClassCall, OutOfMemoryException {
        switch (msg.getAction()) {
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
