package Mediator.Handlers.Scheduler;

import Mediator.Message;
import Mediator.Mediator;
import Os.Scheduler;
import Utils.Command;

public class Schedule implements Command {
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);

        return scheduler.schedule();
    }
}
