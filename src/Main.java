import Mediator.Mediator;
import Mediator.MediatorAction;
import Mediator.MediatorComponent;
import VirtualMachine.*;
import VirtualMachine.Processor.CoreSimple;
import Os.Os;
import Os.Scheduler;
import Os.SchedulerLuan;
import Os.Process;

public class Main {

    public static void main(String[] args) {
        //CLI cli = new CLI();

        final int numCores = 2;
        final Process.Type proc_type = Process.Type.SIMPLE;
        // Criando os componentes básicos para o cenário de simulação
        Mediator mediator = new Mediator();
        Vm <CoreSimple> vm = new VmSimple(numCores,mediator);
        Os kernel = new Os();
        Scheduler scheduler = new SchedulerLuan(kernel.getNewList(), 
                                                kernel.getReadyList(), 
                                                kernel.getWaitingList(), 
                                                kernel.getTerminatedList(),
                                                numCores, mediator);
        //Mediator.setMediator(mediator);
        //SimulatorGui gui = new SimulatorGui();
        //mediator.registerComponent(MediatorComponent.GUI, gui);
       
        mediator.registerComponent(MediatorComponent.VM, vm);
        mediator.registerComponent(MediatorComponent.OS, kernel);
        mediator.registerComponent(MediatorComponent.SCHEDULER, scheduler);
        
        
        mediator.invoke(MediatorAction.KERNEL_NEW_PROCESS, new Object[]{proc_type});
        mediator.invoke(MediatorAction.KERNEL_NEW_PROCESS, new Object[]{proc_type});
        mediator.invoke(MediatorAction.KERNEL_NEW_PROCESS, new Object[]{proc_type});
        mediator.invoke(MediatorAction.KERNEL_NEW_PROCESS, new Object[]{proc_type});
        
        // Executa a simulação
        vm.run();
    }
}
