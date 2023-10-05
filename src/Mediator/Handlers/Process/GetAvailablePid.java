package Mediator.Handlers.Process;

import Mediator.Message;
import Mediator.Mediator;
import Os.Os;
import Utils.Command;

public class GetAvailablePid implements Command {
    @Override
    public Object execute(Message message) {
        Os kernel = (Os) message.getComponents().get(Mediator.Component.OS);
        return kernel.getAvailablePid();
    }
}
