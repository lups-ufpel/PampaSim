package simuladorso.MessageBroker.Handlers.Core;

import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.Processor.Core;
import simuladorso.Os.Process;

import java.util.LinkedList;

public class Execute implements Command {
    @Override
    public Object execute(Message message) {
        LinkedList<Object> params;
        Core core;
        Process process;

        try {
            params = (LinkedList<Object>) message.getParameters().get(MessageParam.OTHERS);
            core = (Core) params.get(0);
            process = (Process) params.get(1);
        } catch (Exception e) {
            Logger.getInstance().error(e.getMessage());
            return null;
        }

        core.execute(process);

        return null;
    }
}
