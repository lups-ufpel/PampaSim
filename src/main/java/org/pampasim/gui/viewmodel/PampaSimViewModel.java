package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import org.pampasim.SimCore.EventInfo;
import org.pampasim.SimCore.SimulatedScenario;
import org.pampasim.SimEntity.ProcessManager;
import org.pampasim.SimEntity.Scheduler;
import org.pampasim.SimResources.Process;
import org.pampasim.gui.scopes.ProcessScope;
import org.pampasim.gui.scopes.SchedulerDialogScope;

public class PampaSimViewModel implements ViewModel {
    public final static String NEW_PROCESS = "NEW_PROCESS";
    public final static String START_PROCESS = "START_PROCESS";
    public final static String FINISH_PROCESS = "FINISH_PROCESS";
    public final static String RUN_PROCESS = "RUN_PROCESS";
    public SimulatedScenario simulatedScenario;

    @InjectScope
    private ProcessScope processScope;
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;

    public PampaSimViewModel() {
        simulatedScenario = new SimulatedScenario();
        ProcessManager kernel = new ProcessManager(simulatedScenario.getSimulation());
        simulatedScenario.setProcessManager(kernel);
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
       var newProcess = new Process(priority,duration,start);
       simulatedScenario.getProcessManager().submitProcess(newProcess);
       this.addProcessListeners(newProcess);
       newProcess.notifyListenersOnCreate();
    }
    public boolean hasProcesses() {
        return true;
    }
    public boolean isSchedulerSet() {
        return simulatedScenario.getScheduler() != null;
    }
    public void setSimulationScheduler() {
        String schedulerName = schedulerDialogScope.getSchedulerNameProperty().getValue();
        switch (schedulerName) {
            case "FCFS", "SJF", "Round Robin", "Priority":
                simulatedScenario.setScheduler(new Scheduler(simulatedScenario.getSimulation()));
                break;
        }
    }
    public Boolean startSimulation() {
        if (!this.isSchedulerSet()) {
            return false;
        }
        return true;
    }

    public void addProcessListeners(Process process) {
        process.addOnCreateListener(this::notifyGuiOnCreatedProcess);
        process.addOnStartListener(this::notifyGuiOnStartProcess);
        process.addOnFinishListener(this::notifyGuiOnFinishedProcess);
        process.addOnUpdateListener(this::notifyGuiOnExecuteProcess);
        //todo: notify on suspend and on resume.
    }

    private void notifyGuiOnCreatedProcess(EventInfo eventInfo) {
        this.publish(NEW_PROCESS);
    }
    private void notifyGuiOnExecuteProcess(EventInfo eventInfo) {
       this.publish(RUN_PROCESS);
    }
    private void notifyGuiOnFinishedProcess(EventInfo eventInfo) {
       this.publish(FINISH_PROCESS);
    }
    private void notifyGuiOnStartProcess(EventInfo eventInfo) {
        this.publish(START_PROCESS);
    }

    public void runSimulation() {

    }
}