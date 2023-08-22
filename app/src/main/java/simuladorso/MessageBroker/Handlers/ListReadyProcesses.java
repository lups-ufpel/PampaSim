package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.Utils.SimpleCommand;

public class ListReadyProcesses implements SimpleCommand {
    private Logger logger;
    public ListReadyProcesses(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {

    }
}
