package simuladorso;

import simuladorso.GUI.SimulatorGui;
import simuladorso.Mediator.Mediator;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Vm.VirtualMachine;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();

        Mediator mediator = new Mediator();

        SimulatorGui.setMediator(mediator);
        SimulatorGui gui = new SimulatorGui();
        mediator.registerComponent(MediatorComponent.GUI, gui);

        VirtualMachineSimple vm = new VirtualMachineSimple(4, mediator);
        mediator.registerComponent(MediatorComponent.VM, vm);

        Thread vmThread = new Thread(vm);
        vmThread.setDaemon(true);
        vmThread.start();

        Thread mediatorThread = new Thread(mediator);
        mediatorThread.setDaemon(true);
        mediatorThread.start();

        gui.run(args);
        //gui.setMediator(mediator);
    }
}