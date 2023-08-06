package simuladorso;

import simuladorso.Command.MainCommand.Invoker;
import simuladorso.Command.MainCommand.Message;
import simuladorso.GUI.App;
import simuladorso.VirtualMachine.VirtualMachine;

/**
 * Classe de teste para o escalonador
 */

public class Main {
    public static void main(String[] args) {
        VirtualMachine vm;
        App gui;
        Thread threadGui;

        if (args.length < 1)
            vm = new VirtualMachine(4);
        else
            vm = new VirtualMachine(Integer.parseInt(args[1]));

        //gui = new App(vm);
        //App.run(args);
        //threadGui = new Thread(gui);
        //threadGui.start();

        vm.run();


        /*
        for (int i = 0; i < 20; i++) {
            Invoker.invoke("Kernel", new Message("newProcess"));
            System.out.println("Processo " + i + " criado");
        }

        new VirtualMachine(4);
        */

        // SchedulerTest schedulerTest = new SchedulerTest();
        // schedulerTest.test();
    }
}
