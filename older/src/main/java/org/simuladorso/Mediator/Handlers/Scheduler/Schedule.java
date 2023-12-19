package org.simuladorso.Mediator.Handlers.Scheduler;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Scheduler;
import org.simuladorso.Utils.Command;

public class Schedule implements Command {
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);
        // Ih si a gente implementasse algo para dizer qual política de escalonamento queremos utilizar ?
        return scheduler.schedule();
    }
}
