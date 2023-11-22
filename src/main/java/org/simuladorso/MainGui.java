package org.simuladorso;

import javafx.stage.Stage;

import org.simuladorso.GUI.SimulatorGui;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Mediator.MediatorDefault;
import org.simuladorso.Os.Os;
import org.simuladorso.VirtualMachine.VmSimple;

public class MainGui {
    public static void main(String[] args) {

        Mediator mediator = new MediatorDefault();

        SimulatorGui.setMediator(mediator);
        SimulatorGui gui = new SimulatorGui();
        mediator.registerComponent(Mediator.Component.GUI, gui);

        VmSimple vm = new VmSimple(4, mediator);
        mediator.registerComponent(Mediator.Component.VM, vm);

        Os os = new Os(mediator);
        mediator.registerComponent(Mediator.Component.OS, os.getKernel());
        mediator.registerComponent(Mediator.Component.SCHEDULER, os.getScheduler());

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