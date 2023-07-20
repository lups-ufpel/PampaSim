package simuladorso;

import simuladorso.Command.MainCommand.Invoker;
import simuladorso.Command.MainCommand.Message;
import simuladorso.VirtualMachine.VirtualMachine;

/**
 * Classe de teste para o escalonador
 */

public class Main {

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            Invoker.invoke("Kernel", new Message("newProcess"));
            System.out.println("Processo " + i + " criado");
        }

        new VirtualMachine(4);


        // SchedulerTest schedulerTest = new SchedulerTest();
        // schedulerTest.test();
    }
}
