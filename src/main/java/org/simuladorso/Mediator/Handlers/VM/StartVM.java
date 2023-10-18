package org.simuladorso.Mediator.Handlers.VM;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Utils.Command;
import org.simuladorso.VirtualMachine.Vm;

public class StartVM implements Command {
    @Override
    public Object execute(Message message) {
        Vm<?>vm = (Vm<?>) message.getComponents().get(Mediator.Component.VM);

        vm.start();

        return null;
    }
}
