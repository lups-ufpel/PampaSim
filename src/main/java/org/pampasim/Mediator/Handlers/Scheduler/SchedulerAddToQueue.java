package org.pampasim.Mediator.Handlers.Scheduler;

import org.pampasim.Mediator.Message;


import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.Scheduler;
import org.pampasim.Utils.Command;
import org.pampasim.Os.Process;

public class SchedulerAddToQueue implements Command{
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);
        LOGGER.debug("SchedulerAddToQueue Scheduler class: {}",scheduler.getClass().getSimpleName());
        Process p = (Process) message.getParameters()[0];
        scheduler.addNewProcess(p);
        return null;
    }
}
