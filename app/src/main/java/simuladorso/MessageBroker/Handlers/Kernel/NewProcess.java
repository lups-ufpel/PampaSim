package simuladorso.MessageBroker.Handlers.Kernel;


import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Os.Kernel;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

import java.util.LinkedList;

public class NewProcess implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getParameters().get(MessageParam.KERNEL);

        try {
            kernel.newProcess();
        } catch (IllegalMethodCall | OutOfMemoryException | IllegalClassCall e) {
            Logger.getInstance().warning("Couldn't create new process : " + e.getMessage());
        }

        return null;
    }
}
