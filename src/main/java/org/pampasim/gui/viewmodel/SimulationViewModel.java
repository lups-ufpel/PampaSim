package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.*;
import org.pampasim.Os.Process;
import org.pampasim.gui.ProcessScope;
import org.pampasim.gui.SchedulerDialogScope;
import org.pampasim.gui.listeners.ProcessEventInfo;
import org.pampasim.gui.model.ProcessMock;

public class SimulationViewModel implements ViewModel {
    public final static String NEW_PROCESS = "NEW_PROCESS";
    public final static String START_PROCESS = "START_PROCESS";
    public final static String FINISH_PROCESS = "FINISH_PROCESS";
    public final static String RUN_PROCESS = "RUN_PROCESS";

    @InjectScope
    private ProcessScope processScope;
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;
    public SimulationViewModel() {
        Mediator.getInstance().registerComponent(this, Mediator.Component.GUI);
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
       var newProcess = (ProcessMock) Mediator.getInstance().getOs().createProcess(Process.Type.SIMPLE,burst,priority,arrival);
       addProcessListeners(newProcess);
       newProcess.notifyOnCreate();
    }
    public void setSimulationScheduler() {
        String schedulerName = schedulerDialogScope.schedulerNamePropertyProperty().getValue();
        Scheduler scheduler = null;
        switch (schedulerName) {
            case "FCFS":
                scheduler = new SchedulerFCFS(1,Mediator.getInstance());
                break;
            case "SJF":
                scheduler = new SchedulerSJF(1,Mediator.getInstance());
                break;
            case "Round Robin":
                scheduler = new SchedulerRoundRobin(1,Mediator.getInstance(),schedulerDialogScope.quantumPropertyProperty().getValue());
                break;
            case "Priority":
                scheduler = new SchedulerPriority(1,Mediator.getInstance());
                break;
        }
        Mediator.getInstance().registerComponent(scheduler,Mediator.Component.SCHEDULER);
    }
    public Boolean startSimulation() {
        Scheduler scheduler = Mediator.getInstance().getScheduler();
        Os kernel = (Os) Mediator.getInstance().retrieveComponent(Mediator.Component.KERNEL);
        kernel.dispatchAll(scheduler);
       return true;
    }
    public void addProcessListeners(ProcessMock processMock) {
        processMock.addOnCreateListener(this::notifyGuiOnCreatedProcess);
        processMock.addOnStartListener(this::notifyGuiOnStartProcess);
        processMock.addOnFinishListener(this::notifyGuiOnFinishedProcess);
        processMock.addOnUpdateListener(this::notifyGuiOnExecuteProcess);
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
        Mediator.getInstance().send(this, Mediator.Action.RUN);
    }
}