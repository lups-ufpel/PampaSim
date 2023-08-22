package simuladorso.MessageBroker.Handlers;

import simuladorso.Kernel.Process;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.Messager;
import simuladorso.Utils.SimpleCommand;
import simuladorso.VirtualMachine.VirtualMachine;

import java.util.HashMap;
import java.util.LinkedList;

public class ListRunningProcesses implements SimpleCommand {
    private Logger logger;
    public ListRunningProcesses(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void execute(Object object) {
        Message msg = (Message) object;
        Messager sender = (Messager) msg.getSender();
        VirtualMachine vm = (VirtualMachine) msg.getReceiver();

        Process[] running = vm.getRunningList();
        LinkedList<Integer> pids = new LinkedList<>();

        for (Process ps : running) {
            if (ps != null)
                pids.add(ps.getPid());
        }

        sender.receive(pids);
    }
}
