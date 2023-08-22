package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.Utils.SimpleCommand;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

public class GetProcessByPid implements SimpleCommand {
    private Logger logger;
    public GetProcessByPid(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {

    }
}
