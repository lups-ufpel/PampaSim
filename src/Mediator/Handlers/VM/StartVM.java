package Mediator.Handlers.VM;

import Mediator.Message;
import Mediator.MediatorComponent;
import Utils.Command;
import VirtualMachine.Vm;

public class StartVM implements Command {
    @Override
    public Object execute(Message message) {
        Vm vm = (Vm) message.getComponents().get(MediatorComponent.VM);

        vm.start();

        return null;
    }
}
