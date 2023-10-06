package Mediator.Handlers.Kernel;


import Mediator.Message;
import Mediator.Mediator;
import Os.Os;
import Os.Process;
import Utils.Command;
//import Utils.Errors.IllegalClassCall;
//import Utils.Errors.IllegalMethodCall;
//import Utils.Errors.OutOfMemoryException;

//import java.util.logging.Logger;

public class NewProcess implements Command {
    @Override
    public Object execute(Message message) {
        Os os = (Os) message.getComponents().get(Mediator.Component.OS);
        Process.Type pt = (Process.Type) message.getParameters()[0];
        switch(pt) {
            case SIMPLE:
                int size = (int) message.getParameters()[1];
                os.newProcess(size);
                break;
            case COMPLETE:
                os.newProcess();
                break;
            default:
                throw new IllegalArgumentException("Invalid process type");
        }
        return null;
    }
}
