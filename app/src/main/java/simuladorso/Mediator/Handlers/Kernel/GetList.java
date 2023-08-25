package simuladorso.Mediator.Handlers.Kernel;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Kernel;
import simuladorso.Utils.Command;

public class GetList implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getComponents().get(MediatorComponent.KERNEL);
        return kernel.getProcessList();
    }
}
