import Mediator.Mediator;
import Mediator.MediatorDefault;
import VirtualMachine.*;
import VirtualMachine.Processor.CoreSimple;
import Os.FCFSSimple;
import Os.Os;
import Os.Scheduler;
import Os.Process;

public class MainTeste {

    static final Process.Type proc_type = Process.Type.SIMPLE;
    static final int numCores = 2;
    public static void main(String[] args){

        // Criando os componentes básicos para o cenário de simulação
        Mediator mediator = new MediatorDefault();
        Os kernel = new Os();
        Scheduler scheduler = new FCFSSimple(kernel.getNewList(), 
                                                kernel.getReadyList(), 
                                                kernel.getWaitingList(), 
                                                kernel.getTerminatedList(),
                                                numCores, mediator);
        Vm <CoreSimple> vm = new VmSimple(numCores, mediator);
        mediator.registerComponent(Mediator.Component.OS, kernel);
        mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 4});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 10});
        mediator.registerComponent(Mediator.Component.VM, vm);
        vm.run();
    }
}
