package org.simuladorso.Mediator.Handlers.Kernel;

import org.simuladorso.Mediator.Message;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;
import org.simuladorso.Utils.Command;
import org.simuladorso.VirtualMachine.Vm;

public class ListRunningProcesses implements Command {
    @Override
    public Object execute(Message message) {
        // Vm extends a generic type Core how can I check which type of core is?
        // CoreLuan core = (CoreLuan) message.getComponents().get(Mediator.Component.CORE);
        // core.getRunningList();
        
        Vm vm  = (Vm) message.getComponents().get(Mediator.Component.VM);

        Process[] running;
        Integer[] pids;

        try {
            running = vm.getRunningList();
        } catch (Exception e) {
            //Logger.getInstance().warning(e.getMessage());
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
