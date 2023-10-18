package org.simuladorso.Utils;

import org.simuladorso.Mediator.Message;

public interface Command {
    public Object execute(Message message);
}
