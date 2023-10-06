package Mediator.Handlers.Kernel;

import Mediator.Mediator;
import Mediator.Message;
import Os.Os;
import Utils.Command;
import Os.Process;

public class GetProcessByPid implements Command {
    @Override
    public Object execute(Message message) {
        Os kernel = (Os) message.getComponents().get(Mediator.Component.OS);
        int pid = (int) message.getParameters()[0];
        Process proc = kernel.getProcess(pid);
        return proc;
    }
}
