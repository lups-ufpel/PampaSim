package simuladorso.Mediator.Handlers.Gui;

import simuladorso.GUI.SimulatorGui;
import simuladorso.Logger.Logger;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Mediator.Message;
import simuladorso.Utils.Command;

import java.io.IOException;

public class UpdateCoresInfo implements Command {
    @Override
    public Object execute(Message message) {
        SimulatorGui gui = (SimulatorGui) message.getComponents().get(MediatorComponent.GUI);

        try {
            gui.getMainAppController().refreshCoresPane();
        } catch (IOException e) {
            Logger.getInstance().error(e.getMessage());
        }

        return null;
    }
}
