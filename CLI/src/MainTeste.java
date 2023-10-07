import Mediator.Mediator;
import Mediator.MediatorDefault;
import VirtualMachine.*;
import VirtualMachine.Processor.Core;
import Os.SchedulerFCFS;
import Os.SchedulerPriority;
import Os.Os;
import Os.Scheduler;
import Os.Process;

public class MainTeste {

    static final Process.Type proc_type = Process.Type.SIMPLE_WITH_PRIORITY;
    static final int numCores = 2;
    
    //Componentes principais
    static Mediator mediator;
    static Os kernel;
    static Scheduler scheduler;
    static  Vm <? extends Core> vm;
    public static void main(String[] args){
        //fcfsTest();
        priorityTest();
    }
    public static void fcfsTest(){
        mediator = new MediatorDefault();
        kernel = new Os();
        scheduler = new SchedulerFCFS(kernel.getNewList(), 
                                                kernel.getReadyList(), 
                                                kernel.getWaitingList(), 
                                                kernel.getTerminatedList(),
                                                numCores, mediator);
        vm = new VmSimple(numCores, mediator);
        registerComponent();
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 4});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 10});
        vm.run();
    }
    public static void priorityTest() {
        mediator = new MediatorDefault();
        kernel = new Os();
        scheduler = new SchedulerPriority(kernel.getNewList(), 
                                                kernel.getReadyList(), 
                                                kernel.getWaitingList(), 
                                                kernel.getTerminatedList(),
                                                numCores, mediator);
        vm = new VmSimple(numCores, mediator);
        registerComponent();
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 4, 3});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 10, 2});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 2, 10});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 5, 1});
        vm.run();
    }
    public static void registerComponent(){
        mediator.registerComponent(Mediator.Component.OS, kernel);
        mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
        mediator.registerComponent(Mediator.Component.VM, vm);
    }
}