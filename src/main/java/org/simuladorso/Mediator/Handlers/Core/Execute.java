package org.simuladorso.Mediator.Handlers.Core;

//import Logger.Logger;
import org.simuladorso.Mediator.Message;
import org.simuladorso.Utils.Command;
import org.simuladorso.VirtualMachine.Processor.Core;
import org.simuladorso.Os.Process;

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