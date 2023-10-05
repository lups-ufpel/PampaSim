package Mediator.Handlers.Core;

import Mediator.Message;
import Mediator.Mediator;
import Utils.Command;
import VirtualMachine.Vm;

public class ListCores implements Command {
    @Override
    public Object execute(Message message) {
        Vm vm = (Vm) message.getComponents().get(Mediator.Component.VM);
        //return vm.getCores();
        throw new UnsupportedOperationException("Vm.getCores() not implemented");
    }
}
