package Mediator.Handlers.Scheduler;

import Mediator.Message;


import Mediator.Mediator;
import Os.Scheduler;
import Utils.Command;
import Os.Process;

public class SchedulerAddToQueue implements Command{
    @Override
    public Object execute(Message message) {
        Scheduler scheduler = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);
        Process p = (Process) message.getParameters()[0];
        scheduler.addNewProcess(p);
        return null;
    }
}
