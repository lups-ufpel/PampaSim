package Mediator.Handlers.Kernel;

import Mediator.Message;
import Mediator.Mediator;
import Os.Os;
import Utils.Command;

public class GetProcess implements Command {
    @Override
    public Object execute(Message message) {
        Os kernel = (Os) message.getComponents().get(Mediator.Component.OS);
        Integer pid;

        try {
            pid = (Integer) message.getParameters()[0];
        } catch (Exception e) {
            return null;
        }

        return kernel.getProcess(pid);
    }
}