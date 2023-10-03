package simuladorso.Mediator.Handlers.Kernel;


import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Kernel;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

public class NewProcess implements Command {
    @Override
    public Object execute(Message message) {
        Kernel kernel = (Kernel) message.getComponents().get(MediatorComponent.KERNEL);

        try {
            kernel.newProcess();
        } catch (IllegalMethodCall | OutOfMemoryException | IllegalClassCall e) {
            Logger.getInstance().warning("Couldn't create new process : " + e.getMessage());
        }

        return null;
    }
}
