import Command.MainCommand.Invoker;
import Command.MainCommand.Message;
import VirtualMachine.VirtualMachine;

/**
 * Classe de teste para o escalonador
 */

public class Main {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            Invoker.invoke("Kernel", new Message("newProcess"));
        }

        new VirtualMachine(4);
    }
}
