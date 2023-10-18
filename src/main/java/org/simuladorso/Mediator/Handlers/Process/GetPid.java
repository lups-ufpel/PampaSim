package org.simuladorso.Mediator.Handlers.Process;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Utils.Command;
import org.simuladorso.Os.Process;
public class GetPid implements Command{
    
    @Override
    public Object execute(Message message) {
        Process p = (Process) message.getParameters()[0];
        return p.getPid();
    }
}
