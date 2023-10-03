package simuladorso.Mediator.Handlers.VM;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.VirtualMachine;

public class StopVM implements Command {
    @Override
    public Object execute(Message message) {
        VirtualMachine vm = (VirtualMachine) message.getComponents().get(MediatorComponent.VM);

        vm.stop();

        return null;
    }
}
