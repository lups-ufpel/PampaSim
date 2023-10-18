package org.simuladorso.Mediator.Handlers.Scheduler;

import org.simuladorso.Mediator.Message;


import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Scheduler;
import org.simuladorso.Utils.Command;
import org.simuladorso.Os.Process;

public class SchedulerAddToQueue implements Command{
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);
        Process p = (Process) message.getParameters()[0];
        scheduler.addNewProcess(p);
        return null;
    }
}
