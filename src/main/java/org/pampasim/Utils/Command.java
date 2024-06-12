package org.pampasim.Utils;

import org.pampasim.Mediator.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public interface Command {

    static final Logger LOGGER = LoggerFactory.getLogger(Command.class.getSimpleName());
    public Object execute(Message message);
}
