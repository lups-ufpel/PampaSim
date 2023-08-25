package simuladorso.Mediator.Handlers.Scheduler;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Scheduler;
import simuladorso.Utils.Command;

public class Schedule implements Command {
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(MediatorComponent.SCHEDULER);

        return scheduler.schedule();
    }
}
