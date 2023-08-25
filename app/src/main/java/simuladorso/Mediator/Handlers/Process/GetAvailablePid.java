package simuladorso.Mediator.Handlers.Process;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Kernel;
import simuladorso.Utils.Command;

public class GetAvailablePid implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getComponents().get(MediatorComponent.KERNEL);
        return kernel.getAvailablePid();
    }
}
