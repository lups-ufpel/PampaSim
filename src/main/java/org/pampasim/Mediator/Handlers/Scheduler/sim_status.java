package org.pampasim.Mediator.Handlers.Scheduler;

import org.pampasim.Mediator.Mediator;
import org.pampasim.Mediator.Message;
import org.pampasim.Os.Scheduler;
import org.pampasim.Utils.Command;

public class sim_status implements Command {

    Scheduler sched;
    @Override
    public Object execute(Message message) {
        sched = (Scheduler) message.getComponents().get(Mediator.Component.SCHEDULER);
        return isSimulationFinished();
    }
    boolean isSimulationFinished(){
        return !(sched.isThereNewProcesses() || sched.isThereReadyProcesses() || sched.isThereRunningProcesses() || sched.isThereWaitingProcesses());
    }
}
