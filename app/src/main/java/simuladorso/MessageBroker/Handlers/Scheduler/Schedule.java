package simuladorso.MessageBroker.Handlers.Scheduler;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Os.Scheduler;
import simuladorso.Utils.Command;

public class Schedule implements Command {
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getParameters().get(MessageParam.SCHEDULER);

        return scheduler.schedule();
    }
}
