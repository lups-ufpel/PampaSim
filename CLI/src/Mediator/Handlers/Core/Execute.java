package Mediator.Handlers.Core;

//import Logger.Logger;
import Mediator.Message;
import Utils.Command;
import VirtualMachine.Processor.Core;
import Os.Process;

public class Execute implements Command {
    @Override
    public Object execute(Message message) {
        Object[] params;
        Core core;
        Process process;

        try {
            params = (Object[]) message.getParameters();
            core = (Core) params[1];
            process = (Process) params[0];
        } catch (Exception e) {
            //Logger.getInstance().error(e.getMessage());
            return null;
        }

        core.execute(process);

        return null;
    }
}