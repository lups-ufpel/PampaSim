/*

    * Classname
    *
    * Version information
    *
    * Date
    *
    * Copyright notice
 */
package org.pampaos;

import org.simuladorso.GUI.SimulatorGui;
import org.simuladorso.Mediator.*;
import org.simuladorso.Os.Process;
import org.simuladorso.Os.*;
import org.simuladorso.Utils.Statistics.ExecutionTable;
import org.simuladorso.VirtualMachine.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {


    static final Logger LOGGER = LoggerFactory.getLogger("Main");
    static final Process.Type PROCESS_TYPE = Process.Type.SIMPLE;
    static final int CORES = 2;
    
    static Mediator mediator = new MediatorDefault();
    static Os kernel;
    static Scheduler scheduler;
    static  Vm vm;
    public static void main(String[] args){

        LOGGER.info("==================██Starting PampaOS Simulator██==================\n");
        //preemptiveRoundRobinTest();
        //priorityTest();

        //sjfTest();
        fcfsTest();
        //guiTest(args);
    }
    public static void preemptiveRoundRobinTest(){
        kernel = new Os(mediator);
        scheduler = new SchedulerRoundRobin(CORES, mediator, 2);
        vm = new VmSimple(CORES, mediator);
        registerComponent();
        createProcesses();
        vm.run();
    }
    public static void priorityTest(){

        kernel = new Os(mediator);
        scheduler = new SchedulerPriority(CORES, mediator);
        vm = new VmSimple(CORES, mediator);
        registerComponent();
        createProcesses();
        vm.run();
    }
    public static void sjfTest(){
        kernel = new Os(mediator);
        scheduler = new SchedulerSJF(CORES, mediator);
        vm = new VmSimple(CORES, mediator);
        registerComponent();
        createProcesses();
        vm.run();

    }
    public static void fcfsTest(){
        kernel = new Os(mediator);
        scheduler = new SchedulerFCFS(CORES, mediator);
        vm = new VmSimple(CORES, mediator);
        registerComponent();
        createProcesses();
        ExecutionTable.showSystemComponents((MediatorDefault) mediator);
        vm.run();
    }

    public static void guiTest(String[] args){
        SimulatorGui gui = new SimulatorGui();
        kernel = new Os(mediator);
        scheduler = new SchedulerFCFS(CORES, mediator);
        vm = new VmSimple(CORES, mediator);
        registerComponent();
        mediator.registerComponent(Mediator.Component.GUI, gui);
        createProcesses();
        ExecutionTable.showSystemComponents((MediatorDefault) mediator);
        vm.run();
        gui.run(args);
    }

    public static void createProcesses(){
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{PROCESS_TYPE, 5, 2, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{PROCESS_TYPE, 2, 3, 0});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{PROCESS_TYPE, 4, 1, 1});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{PROCESS_TYPE, 1, 4, 3});
        mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS, new Object[]{PROCESS_TYPE, 2, 5, 5});
    }
    public static void registerComponent(){
        mediator.registerComponent(Mediator.Component.OS, kernel);
        mediator.registerComponent(Mediator.Component.SCHEDULER, scheduler);
        mediator.registerComponent(Mediator.Component.VM, vm);
    }
}


