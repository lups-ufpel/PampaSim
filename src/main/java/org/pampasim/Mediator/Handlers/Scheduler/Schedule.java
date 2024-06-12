package org.pampasim.Mediator.Handlers.Scheduler;

import org.pampasim.Mediator.Message;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.Scheduler;
import org.pampasim.Utils.Command;

public class Schedule implements Command {
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);
        // Ih si a gente implementasse algo para dizer qual política de escalonamento queremos utilizar ?
        return scheduler.schedule();
    }
}
