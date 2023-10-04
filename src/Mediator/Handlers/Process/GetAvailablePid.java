package Mediator.Handlers.Process;

import Mediator.Message;
import Mediator.MediatorComponent;
import Os.Os;
import Utils.Command;

public class GetAvailablePid implements Command {
    @Override
    public Object execute(Message message) {
        Os kernel = (Os) message.getComponents().get(MediatorComponent.OS);
        return kernel.getAvailablePid();
    }
}
