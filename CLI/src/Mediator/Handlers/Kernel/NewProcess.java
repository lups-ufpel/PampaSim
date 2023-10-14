package Mediator.Handlers.Kernel;


import Mediator.Message;
import Mediator.Mediator;
import Os.Os;
import Utils.Command;
//import Utils.Errors.IllegalClassCall;
//import Utils.Errors.IllegalMethodCall;
//import Utils.Errors.OutOfMemoryException;

//import java.util.logging.Logger;

public class NewProcess implements Command {
    @Override
    public Object execute(Message message) {
        Os os = (Os) message.getComponents().get(Mediator.Component.OS);
        Object[] attributes = message.getParameters();
        os.createNewProcess(attributes);
        return null;
    }
}
