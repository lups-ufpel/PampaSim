package simuladorso.Mediator.Handlers.Core;

import simuladorso.Logger.Logger;
import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.Processor.Core;
import simuladorso.Os.Process;

import java.util.LinkedList;

public class Execute implements Command {
    @Override
    public Object execute(Message message) {
        Object[] params;
        Core core;
        Process process;

        try {
            params = (Object[]) message.getParameters();
            core = (Core) params[0];
            process = (Process) params[1];
        } catch (Exception e) {
            Logger.getInstance().error(e.getMessage());
            return null;
        }

        core.execute(process);

        return null;
    }
}
