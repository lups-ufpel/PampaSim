package org.simuladorso.Mediator.Handlers.VM;

import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.Message;
import org.simuladorso.Utils.Command;
import org.simuladorso.VirtualMachine.Vm;

public class GetTick implements Command {

    @Override
    public Object execute(Message message) {

        Vm<?> vm = (Vm<?>) message.getComponents().get(Mediator.Component.VM);
        return vm.SIM_CLOCK.getTick();
    }
}
