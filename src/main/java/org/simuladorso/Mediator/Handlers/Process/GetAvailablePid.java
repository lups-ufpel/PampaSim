package org.simuladorso.Mediator.Handlers.Process;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Utils.Command;
import org.simuladorso.Os.Os;
public class GetAvailablePid implements Command {
    @Override
    public Object execute(Message message) {
        Os kernel = (Os) message.getComponents().get(Mediator.Component.OS);
        return kernel.getAvailablePid();
    }
}
