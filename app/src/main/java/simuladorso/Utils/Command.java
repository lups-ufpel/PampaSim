package simuladorso.Utils;

import simuladorso.MessageBroker.Message;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

public interface Command {
    public Object execute(Message message) throws IllegalMethodCall, IllegalClassCall, OutOfMemoryException;
}
