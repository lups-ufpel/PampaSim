package simuladorso.Utils;

import simuladorso.Mediator.Message;

public interface Command {
    public Object execute(Message message);
}
