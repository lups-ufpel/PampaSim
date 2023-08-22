package simuladorso.MessageBroker.Handlers.Gui;

import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageParam;
import simuladorso.Os.Process;
import simuladorso.Logger.Logger;
import simuladorso.Utils.Command;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.LinkedList;

public class ListRunningProcesses implements Command {
    @Override
    public Object execute(Message message) {
        VirtualMachine vm = (VirtualMachine) message.getParameters().get(MessageParam.VM);

        Process[] running;
        LinkedList<Integer> pids = new LinkedList<>();

        try {
            running = vm.getRunningList();
        } catch (Exception e) {
            Logger.getInstance().warning(e.getMessage());
            return null;
        }

        for (Process ps : running) {
            if (ps != null)
                pids.add(ps.getPid());
        }

        return pids;
    }
}
