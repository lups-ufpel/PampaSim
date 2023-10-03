import java.util.ArrayList;
import VirtualMachine.Sbyte;
import VirtualMachine.*;
import Kernel.*;
import Kernel.Process;
/**
 * Classe de teste para o escalonador
 */

public class Main {

    static int i = 0;
    static ArrayList<Sbyte> memory;

    public static void main(String[] args) {

        Kernel kernel = new Kernel();
        
        kernel.newProcess(Process.Type.SIMPLE);
        kernel.newProcess(Process.Type.SIMPLE);
        kernel.newProcess(Process.Type.SIMPLE);
        
        VmAbstract simple = new VirtualMachineSimple(1);
        simple.run();
    }
}
