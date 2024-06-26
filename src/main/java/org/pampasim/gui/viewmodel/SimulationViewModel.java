package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import org.pampasim.Os.*;
import org.pampasim.Os.Process;
import org.pampasim.VirtualMachine.VmSimple;
import org.pampasim.gui.ProcessScope;
import org.pampasim.gui.SchedulerDialogScope;
import org.pampasim.gui.listeners.ProcessEventInfo;

public class SimulationViewModel implements ViewModel {
    public final static String NEW_PROCESS = "NEW_PROCESS";
    public final static String START_PROCESS = "START_PROCESS";
    public final static String FINISH_PROCESS = "FINISH_PROCESS";
    public final static String RUN_PROCESS = "RUN_PROCESS";

    private final Os simOs;
    private final VmSimple vmSimple;
    private Scheduler scheduler;

    @InjectScope
    private ProcessScope processScope;
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;
    public SimulationViewModel() {
        simOs = new Os();
        vmSimple = new VmSimple(1);
    }
    public SchedulerDialogScope getSchedulerScope() {
        return schedulerDialogScope;
    }
    public ProcessScope getProcessScope() {
        return processScope;
    }
    public void createNewProcess() {
       var burst = processScope.getBurstProperty().getValue();
       var priority = processScope.getDurationProperty().getValue();
       var arrival = processScope.getDurationProperty().getValue();
       Process newProcess = simOs.createProcess(Process.Type.SIMPLE,burst,priority,arrival);
       addProcessListeners(newProcess);
       newProcess.notifyListenersOnCreate();
    }
    public void setSimulationScheduler() {
        String schedulerName = schedulerDialogScope.schedulerNamePropertyProperty().getValue();
        switch (schedulerName) {
            case "FCFS":
                scheduler = new SchedulerFCFS(1);
                break;
            case "SJF":
                scheduler = new SchedulerSJF(1);
                break;
            case "Round Robin":
                scheduler = new SchedulerRoundRobin(1,schedulerDialogScope.quantumPropertyProperty().getValue());
                break;
            case "Priority":
                scheduler = new SchedulerPriority(1);
                break;
        }
        vmSimple.setScheduler(scheduler);
    }
    public Boolean startSimulation() {
        simOs.dispatchAll(scheduler);
       return true;
    }
    public void addProcessListeners(Process process) {
        process.addOnCreateListener(this::notifyGuiOnCreatedProcess);
        process.addOnStartListener(this::notifyGuiOnStartProcess);
        process.addOnFinishListener(this::notifyGuiOnFinishedProcess);
        process.addOnUpdateListener(this::notifyGuiOnExecuteProcess);
        //todo: notify on suspend and on resume.
    }
    private void notifyGuiOnExecuteProcess(ProcessEventInfo eventInfo) {
       this.publish(RUN_PROCESS);
    }
    private void notifyGuiOnFinishedProcess(ProcessEventInfo eventInfo) {
       this.publish(FINISH_PROCESS);
    }
    private void notifyGuiOnStartProcess(ProcessEventInfo eventInfo) {
        this.publish(START_PROCESS);
    }
    private void notifyGuiOnCreatedProcess(ProcessEventInfo eventInfo) {
        this.publish(NEW_PROCESS);
    }
    /**
     * --- OLDER METHODS THAT USE MEDIATOR GOES BELOW  \:/
     */
    public void runSimulation() {
       this.vmSimple.run();
    }
}