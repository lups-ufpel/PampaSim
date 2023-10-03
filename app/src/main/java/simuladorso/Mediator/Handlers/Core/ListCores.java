package simuladorso.Mediator.Handlers.Core;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Utils.Command;
import simuladorso.Vm.VirtualMachine;

public class ListCores implements Command {
    @Override
    public Object execute(Message message) {
        VirtualMachine vm = (VirtualMachine) message.getComponents().get(MediatorComponent.VM);
        return vm.getCores();
    }
}
