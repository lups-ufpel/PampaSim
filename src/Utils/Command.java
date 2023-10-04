package Utils;

import Mediator.Message;

public interface Command {
    public Object execute(Message message);
}
