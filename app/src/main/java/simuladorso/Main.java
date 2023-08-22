package simuladorso;

import simuladorso.GUI.SimulatorGui;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.MessageBroker;
import simuladorso.VirtualMachine.VirtualMachine;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();

        Logger.getInstance().subscribe(cli);

        MessageBroker mb = MessageBroker.getInstance();

        SimulatorGui gui = new SimulatorGui();

        VirtualMachine vm = new VirtualMachine(4);

        mb.setVm(vm);
        mb.setKernel(vm.getOs().getKernel());
        mb.setScheduler(vm.getOs().getScheduler());

        Thread vmThread = new Thread(vm);
        vmThread.setDaemon(true);
        vmThread.start();

        gui.run(args);
    }
}