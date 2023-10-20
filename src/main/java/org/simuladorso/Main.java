package org.simuladorso;

import org.simuladorso.Mediator.*;
import org.simuladorso.Os.Process;
import org.simuladorso.Os.*;
import org.simuladorso.VirtualMachine.Processor.*;
import org.simuladorso.VirtualMachine.*;


public class Main {

    static final Process.Type proc_type = Process.Type.SIMPLE;
    static final int NUMCORES = 2;
    static Mediator mediator;
    static Os kernel;
    static Scheduler scheduler;
    static  Vm <? extends Core> vm;
    public static void main(String[] args){
        fcfsTest();
    }
    public static void fcfsTest(){

        mediator = new MediatorDefault();
        kernel = new Os(mediator);
        scheduler = new SchedulerFCFS(NUMCORES, mediator);
        vm = new VmSimple(NUMCORES, mediator);
        registerComponent();
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 5, 1, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 4, 1, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 8, 1, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{proc_type, 2, 1, 0});
        vm.run();
    }
    public static void registerComponent(){
        mediator.registerComponent(Mediator.Component.OS, kernel);
        mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
        mediator.registerComponent(Mediator.Component.VM, vm);
    }
}


