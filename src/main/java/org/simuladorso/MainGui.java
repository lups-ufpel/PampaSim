package org.simuladorso;

import javafx.stage.Stage;

import org.simuladorso.GUI.SimulatorGui;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.VirtualMachine.VmSimple;

public class MainGui {
    public static void main(String[] args) {

        Mediator mediator = new Mediator();

        SimulatorGui.setMediator(mediator);
        SimulatorGui gui = new SimulatorGui();
        mediator.registerComponent(MediatorComponent.GUI, gui);

        VmSimple vm = new VmSimple(4, mediator);
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