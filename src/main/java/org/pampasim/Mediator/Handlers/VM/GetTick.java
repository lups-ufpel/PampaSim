package org.pampasim.Mediator.Handlers.VM;

import org.pampasim.Mediator.Mediator;
import org.pampasim.Mediator.Message;
import org.pampasim.Utils.Command;
import org.pampasim.VirtualMachine.Vm;

public class GetTick implements Command {

    @Override
    public Object execute(Message message) {

        Vm vm = (Vm) message.getComponents().get(Mediator.Component.VM);
        return vm.SIM_CLOCK.getTick();
    }
}
