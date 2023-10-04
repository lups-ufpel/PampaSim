package Mediator.Handlers.Kernel;


import Mediator.Message;
import Mediator.MediatorComponent;
import Os.Os;
import Os.Process;
import Utils.Command;
import Utils.Errors.IllegalClassCall;
import Utils.Errors.IllegalMethodCall;
import Utils.Errors.OutOfMemoryException;

import java.util.logging.Logger;

public class NewProcess implements Command {
    @Override
    public Object execute(Message message) {
        Os os = (Os) message.getComponents().get(MediatorComponent.OS);
        Process.Type pt = (Process.Type) message.getParameters()[0];

        os.newProcess(pt);

        return null;
    }
}
