import Mediator.Mediator;
import Mediator.MediatorDefault;
import VirtualMachine.*;
import VirtualMachine.Processor.Core;
import Os.Os;
import Os.Scheduler;
import Os.SchedulerFCFS;
import Os.Process;

public class MainTeste {

    static final Process.Type proc_type = Process.Type.SIMPLE;
    static final int numCores = 2;
    
    //Componentes principais
    static Mediator mediator;
    static Os kernel;
    static Scheduler scheduler;
    static  Vm <? extends Core> vm;
    public static void main(String[] args){
        fcfsTest();
        //priorityTest();
        //SJFTest();
    }
    public static void fcfsTest(){
        mediator = new MediatorDefault();
        kernel = new Os();
        scheduler = new SchedulerFCFS(1, mediator);
        vm = new VmSimple(numCores, mediator);
        registerComponent();
        // (tipo, burstTime, prioridade, arrivaltime)
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 5, 1, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 10, 1, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 3, 1, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 1, 1, 0});
        vm.run();
    }
    // public static void priorityTest() {
    //     mediator = new MediatorDefault();
    //     kernel = new Os();
    //     scheduler = new SchedulerPriority(numCores, mediator);
    //     vm = new VmSimple(numCores, mediator);
    //     registerComponent();
    //     mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 4, 3});
    //     mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 10, 2});
    //     mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 2, 10});
    //     mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 5, 1});
    //     vm.run();
    // }
    public static void registerComponent(){
        mediator.registerComponent(Mediator.Component.OS, kernel);
        mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
        mediator.registerComponent(Mediator.Component.VM, vm);
    }
    // public static void SJFTest(){
    //     mediator = new MediatorDefault();
    //     kernel = new Os();
    //     scheduler = new SchedulerSJF(kernel.getNewList(), 
    //                                             kernel.getReadyList(), 
    //                                             kernel.getWaitingList(), 
    //                                             kernel.getTerminatedList(),
    //                                             numCores, mediator);
    //     vm = new VmSimple(numCores, mediator);
    //     registerComponent();
    //     mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 10, 1,0});
    //     //mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 6});
    //     //mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 4});
    //     //mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 2});
    //     vm.run();                                   
    // }
}