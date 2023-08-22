package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.Messager;
import simuladorso.Utils.SimpleCommand;
import simuladorso.VirtualMachine.VirtualMachine;

public class GetAvailablePid implements SimpleCommand {
    private Logger logger;

    public GetAvailablePid(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {
        Message msg = (Message) object;

        Messager sender = (Messager) msg.getSender();
        VirtualMachine vm = (VirtualMachine) msg.getReceiver();



    }
}
