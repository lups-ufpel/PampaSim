package Mediator.Handlers.Kernel;

import Mediator.Message;
import Mediator.MediatorComponent;
import Os.Os;
import Utils.Command;

public class GetList implements Command {
    @Override
    public Object execute(Message message) {
        Os os = (Os) message.getComponents().get(MediatorComponent.OS);
        return os.getProcessList();
    }
}
