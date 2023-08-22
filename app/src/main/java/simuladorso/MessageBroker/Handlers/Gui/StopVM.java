package simuladorso.MessageBroker.Handlers.Gui;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.LinkedList;

public class StopVM implements Command {
    @Override
    public Object execute(Message message) {
        VirtualMachine vm = (VirtualMachine) message.getParameters().get(MessageParam.VM);

        vm.stop();

        return null;
    }
}
