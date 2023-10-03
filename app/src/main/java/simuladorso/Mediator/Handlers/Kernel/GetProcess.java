package simuladorso.Mediator.Handlers.Kernel;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Kernel;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;

public class GetProcess implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getComponents().get(MediatorComponent.KERNEL);
        Integer pid;

        try {
            pid = (Integer) message.getParameters()[0];
        } catch (Exception e) {
            Logger.getInstance().error("Couldn't cast parameters : " + e.getMessage());
            return null;
        }

        return kernel.getProcess(pid);
    }
}