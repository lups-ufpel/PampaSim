package simuladorso.Mediator.Handlers.Kernel;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.OS;
import simuladorso.Utils.Command;

public class GetProcess implements Command {
    @Override
    public Object execute(Message message) {
        OS kernel = (OS) message.getComponents().get(MediatorComponent.OS);
        Integer pid;

        try {
            pid = (Integer) message.getParameters()[0];
        } catch (Exception e) {
            return null;
        }

        return kernel.getProcess(pid);
    }
}