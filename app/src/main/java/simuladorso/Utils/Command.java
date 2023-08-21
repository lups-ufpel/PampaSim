package simuladorso.Utils;

import simuladorso.MessageBroker.Message;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;

public interface Command {
    public Object execute(Message message) throws IllegalMethodCall, IllegalClassCall;
}
