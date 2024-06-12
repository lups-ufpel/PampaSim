package org.pampasim.Mediator.Handlers.Process;

import org.pampasim.Mediator.Message;
import org.pampasim.Utils.Command;
import org.pampasim.Os.Process;
public class GetPid implements Command{
    
    @Override
    public Object execute(Message message) {
        Process p = (Process) message.getParameters()[0];
        return p.getPid();
    }
}
