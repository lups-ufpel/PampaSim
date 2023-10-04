package Mediator.Handlers.Core;

import Mediator.Message;
import Mediator.MediatorComponent;
import Utils.Command;
import VirtualMachine.Vm;

public class ListCores implements Command {
    @Override
    public Object execute(Message message) {
        Vm vm = (Vm) message.getComponents().get(MediatorComponent.VM);
        //return vm.getCores();
        throw new UnsupportedOperationException("Vm.getCores() not implemented");
    }
}
