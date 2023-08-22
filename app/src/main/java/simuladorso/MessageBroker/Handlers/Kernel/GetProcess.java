package simuladorso.MessageBroker.Handlers.Kernel;

import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Os.Kernel;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;

import java.util.LinkedList;

public class GetProcess implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getParameters().get(MessageParam.KERNEL);
        Integer pid;

        try {
            pid = (Integer) message.getParameters().get(MessageParam.OTHERS);
        } catch (Exception e) {
            Logger.getInstance().error("Couldn't cast parameters : " + e.getMessage());
            return null;
        }

        return kernel.getProcess(pid);
    }
}