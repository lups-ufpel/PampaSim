package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.Utils.SimpleCommand;

public class ListTerminatedProcesses implements SimpleCommand {
    private Logger logger;
    public ListTerminatedProcesses(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {

    }
}