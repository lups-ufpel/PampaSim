package org.simuladorso.Mediator.Handlers.Kernel;


import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Utils.Command;


public class NewProcess implements Command {
    @Override
    public Object execute(Message message) {
        Os os = (Os) message.getComponents().get(Mediator.Component.OS);
        Object[] attributes = message.getParameters();
        os.createNewProcess(attributes);
        return null;
    }
}
