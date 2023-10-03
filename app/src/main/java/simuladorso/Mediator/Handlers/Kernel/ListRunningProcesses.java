package simuladorso.Mediator.Handlers.Kernel;

import simuladorso.Mediator.Message;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Process;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.LinkedList;

public class ListRunningProcesses implements Command {
    @Override
    public Object execute(Message message) {
        VirtualMachine vm = (VirtualMachine) message.getComponents().get(MediatorComponent.VM);

        Process[] running;
        Integer[] pids;

        try {
            running = vm.getRunningList();
        } catch (Exception e) {
            Logger.getInstance().warning(e.getMessage());
            return null;
        }

        pids = new Integer[running.length];

        for (int i = 0; i < running.length; i++) {
            if (running[i] != null)
                pids[i] = running[i].getPid();
            else
                pids[i] = null;
        }

        return pids;
    }
}
