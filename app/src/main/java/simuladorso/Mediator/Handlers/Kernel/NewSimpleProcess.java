package simuladorso.Mediator.Handlers.Kernel;


import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.OS;
import simuladorso.Os.Process;
import simuladorso.Utils.Command;
import simuladorso.Utils.Errors.IllegalClassCall;
import simuladorso.Utils.Errors.IllegalMethodCall;
import simuladorso.Utils.Errors.OutOfMemoryException;

import java.util.logging.Logger;

public class NewSimpleProcess implements Command {
    @Override
    public Object execute(Message message) {
        OS os = (OS) message.getComponents().get(MediatorComponent.OS);
        Process.Type pt = (Process.Type) message.getParameters()[0];

        os.newProcess(Process.Type.SIMPLE);

        return null;
    }
}
