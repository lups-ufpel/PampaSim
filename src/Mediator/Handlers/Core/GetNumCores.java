package Mediator.Handlers.Core;

import Mediator.Message;
import Mediator.Mediator;
import Utils.Command;
import VirtualMachine.Vm;

public class GetNumCores implements Command {
    @Override
    public Object execute(Message message) {
        Vm vm = (Vm) message.getComponents().get(Mediator.Component.VM);
        return vm.getNumCores();
    }
}
