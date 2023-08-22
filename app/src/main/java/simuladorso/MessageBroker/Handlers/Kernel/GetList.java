package simuladorso.MessageBroker.Handlers.Kernel;

import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Os.Kernel;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;

import java.util.LinkedList;

public class GetList implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getParameters().get(MessageParam.KERNEL);
        return kernel.getProcessList();
    }
}
