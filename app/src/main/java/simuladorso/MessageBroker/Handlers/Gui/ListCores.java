package simuladorso.MessageBroker.Handlers.Gui;

import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.VirtualMachine;

public class ListCores implements Command {
    @Override
    public Object execute(Message message) {
        VirtualMachine vm = (VirtualMachine) message.getParameters().get(MessageParam.VM);
        return vm.getCores();
    }
}
