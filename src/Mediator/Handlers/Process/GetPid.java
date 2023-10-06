package Mediator.Handlers.Process;

import Mediator.Message;
import Utils.Command;
import Os.Process;
public class GetPid implements Command{
    
    @Override
    public Object execute(Message message) {
        Process p = (Process) message.getParameters()[0];
        return p.getPid();
    }
}
