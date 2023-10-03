package simuladorso.Mediator.Handlers.Kernel;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.OS;
import simuladorso.Utils.Command;

public class GetList implements Command {
    @Override
    public Object execute(Message message) {
        OS os = (OS) message.getComponents().get(MediatorComponent.OS);
        return os.getProcessList();
    }
}
