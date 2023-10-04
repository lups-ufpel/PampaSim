package Mediator.Handlers.Scheduler;

import Mediator.Message;
import Mediator.MediatorComponent;
import Os.Scheduler;
import Utils.Command;

public class Schedule implements Command {
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(MediatorComponent.SCHEDULER);

        return scheduler.schedule();
    }
}
