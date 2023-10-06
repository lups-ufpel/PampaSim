package Mediator.Handlers.VM;

import Mediator.Message;
import Mediator.Mediator;
import Utils.Command;
import VirtualMachine.Vm;
import VirtualMachine.Processor.Core;

public class StopVM implements Command {
    @Override
    public Object execute(Message message) {
        Vm<?> vm = (Vm<?>) message.getComponents().get(Mediator.Component.VM);

        vm.stop();

        return null;
    }
}
