package org.simuladorso.Mediator.Handlers.Kernel;

import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.Message;
import org.simuladorso.Os.Os;
import org.simuladorso.Utils.Command;
import org.simuladorso.Os.Process;

public class GetProcessByPid implements Command {
    @Override
    public Object execute(Message message) {
        Os kernel = (Os) message.getComponents().get(Mediator.Component.OS);
        int pid = (int) message.getParameters()[0];
        Process proc = kernel.getProcess(pid);
        return proc;
    }
}
