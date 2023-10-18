package org.simuladorso.Mediator.Handlers.Kernel;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Utils.Command;

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