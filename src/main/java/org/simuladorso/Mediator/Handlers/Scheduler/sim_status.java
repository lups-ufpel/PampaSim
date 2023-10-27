package org.simuladorso.Mediator.Handlers.Scheduler;

import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.Message;
import org.simuladorso.Os.Scheduler;
import org.simuladorso.Utils.Command;

import javax.swing.plaf.synth.SynthCheckBoxUI;

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
