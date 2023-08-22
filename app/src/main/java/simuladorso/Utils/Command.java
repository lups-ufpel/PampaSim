package simuladorso.Utils;

import simuladorso.MessageBroker.Message;

public interface Command {
    public Object execute(Message message);
}
