package org.pampasim.Mediator.Handlers.VM;

import org.pampasim.Mediator.Message;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Utils.Command;
import org.pampasim.VirtualMachine.Vm;

public class StopVM implements Command {
    @Override
    public Object execute(Message message) {
        Vm vm = (Vm) message.getComponents().get(Mediator.Component.VM);

        vm.stop();

        return null;
    }
}
