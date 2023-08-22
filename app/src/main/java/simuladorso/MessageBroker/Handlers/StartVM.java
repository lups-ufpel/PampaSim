package simuladorso.MessageBroker.Handlers;

import simuladorso.Logger.Logger;
import simuladorso.Utils.SimpleCommand;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.HashMap;

public class StartVM implements SimpleCommand {
    private Logger logger;

    public StartVM(Logger logger) {
        this.logger = logger;
    }
    @Override
    public void execute(Object object) {
        try {
            HashMap<String, Object> params = (HashMap<String, Object>) object;
            VirtualMachine vm = (VirtualMachine) params.get("VM");
            vm.stop();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }
}
