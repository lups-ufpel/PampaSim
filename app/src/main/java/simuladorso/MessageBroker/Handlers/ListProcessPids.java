package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.Utils.SimpleCommand;

import java.util.List;

public class ListProcessPids implements SimpleCommand {
    private Logger logger;

    public ListProcessPids(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {
        Message msg = (Message) object;


    }
}
