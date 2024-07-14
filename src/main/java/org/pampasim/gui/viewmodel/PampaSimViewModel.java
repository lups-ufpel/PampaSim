package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import org.pampasim.Os.*;
import org.pampasim.Os.Process;
import org.pampasim.VirtualMachine.VmSimple;
import org.pampasim.gui.scopes.ProcessScope;
import org.pampasim.gui.scopes.SchedulerDialogScope;
import org.pampasim.gui.listeners.ProcessEventInfo;

public class PampaSimViewModel implements ViewModel {
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
    public PampaSimViewModel() {
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
       var start = processScope.getStartTimeProperty().getValue();
       var duration = processScope.getDurationProperty().getValue();
       var priority = processScope.getDurationProperty().getValue();

       Process newProcess = simOs.createProcess(Process.Type.SIMPLE, duration, priority, start);
       this.addProcessListeners(newProcess);
       newProcess.notifyListenersOnCreate();
    }

    public boolean hasProcesses() {
        return true;
    }

    public boolean isSchedulerSet() {
        return this.scheduler != null;
    }

    public void setSimulationScheduler() {
        String schedulerName = schedulerDialogScope.getSchedulerNameProperty().getValue();
        switch (schedulerName) {
            case "FCFS":
                scheduler = new SchedulerFCFS(1);
                break;
            case "SJF":
                scheduler = new SchedulerSJF(1);
                break;
            case "Round Robin":
                scheduler = new SchedulerRoundRobin(1, schedulerDialogScope.getQuantumProperty().getValue());
                break;
            case "Priority":
                scheduler = new SchedulerPriority(1);
                break;
        }
        vmSimple.setScheduler(scheduler);
    }
    public Boolean startSimulation() {
        if (!this.isSchedulerSet()) {
            return false;
        }

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