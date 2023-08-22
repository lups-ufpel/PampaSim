package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.Utils.SimpleCommand;

public class ListNewProcesses implements SimpleCommand {
    private Logger logger;
    public ListNewProcesses(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {

    }
}
