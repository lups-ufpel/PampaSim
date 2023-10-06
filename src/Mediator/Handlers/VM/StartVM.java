package Mediator.Handlers.VM;

import Mediator.Message;
import Mediator.Mediator;
import Utils.Command;
import VirtualMachine.Vm;

public class StartVM implements Command {
    @Override
    public Object execute(Message message) {
        Vm<?>vm = (Vm<?>) message.getComponents().get(Mediator.Component.VM);

        vm.start();

        return null;
    }
}
