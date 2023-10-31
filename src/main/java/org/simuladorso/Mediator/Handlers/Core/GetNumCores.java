package org.simuladorso.Mediator.Handlers.Core;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Utils.Command;
import org.simuladorso.VirtualMachine.Vm;

public class GetNumCores implements Command {
    @Override
    public Object execute(Message message) {

        //The unbounded wildcard ? is useful in cases where it doesn't matter what the generic type is.
        Vm vm = (Vm) message.getComponents().get(Mediator.Component.VM);
        return vm.getNumCores();
    }
}
