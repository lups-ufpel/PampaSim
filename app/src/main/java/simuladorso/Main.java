package simuladorso;

import javafx.stage.Stage;
import simuladorso.GUI.SimulatorGui;
import simuladorso.Logger.Logger;
import simuladorso.Mediator.Mediator;
import simuladorso.Mediator.MediatorAction;
import simuladorso.Mediator.MediatorComponent;
import simuladorso.Os.Os;
import simuladorso.VirtualMachine.VirtualMachine;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();

        Logger.getInstance().subscribe(cli);

        Mediator mediator = new Mediator();

        SimulatorGui.setMediator(mediator);
        SimulatorGui gui = new SimulatorGui();
        mediator.registerComponent(MediatorComponent.GUI, gui);

        VirtualMachine vm = new VirtualMachine(4, mediator);
        mediator.registerComponent(MediatorComponent.VM, vm);

        Os os = new Os(mediator);
        mediator.registerComponent(MediatorComponent.KERNEL, os.getKernel());
        mediator.registerComponent(MediatorComponent.SCHEDULER, os.getScheduler());

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