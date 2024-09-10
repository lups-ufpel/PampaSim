package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.pampasim.SimCore.EventInfo;
import org.pampasim.SimCore.ProcessEventInfo;
import org.pampasim.SimCore.SimulatedScenario;
import org.pampasim.SimEntity.ProcessManager;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimEntity.Scheduler;
import org.pampasim.SimResources.Process;
import org.pampasim.SimResources.ProcessorCore;
import org.pampasim.gui.ViewModelEvents.ProcessFinishEvent;
import org.pampasim.gui.ViewModelEvents.ProcessReadyEvent;
import org.pampasim.gui.ViewModelEvents.ProcessStartRunningEvent;
import org.pampasim.gui.scopes.ProcessScope;
import org.pampasim.gui.scopes.SchedulerDialogScope;

public class PampaSimViewModel implements ViewModel {
    private final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    private final ObservableList<ProcessViewModel> processes = FXCollections.observableArrayList();

    @InjectScope
    private ProcessScope processScope;
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;

    public SimulatedScenario simulatedScenario;

    public PampaSimViewModel() {
        simulatedScenario = new SimulatedScenario();
        ProcessManager kernel = new ProcessManager(simulatedScenario.getSimulation());
        ProcessorCore core = new ProcessorCore(100);
        Processor processor = new Processor(simulatedScenario.getSimulation(), core);
        simulatedScenario.setProcessManager(kernel);
    }
    public ObservableList<ProcessViewModel> getProcesses() {
        return processes;
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
       var priority = processScope.getPriorityProperty().getValue();
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
    public void startSimulation() {
        if (!isSchedulerSet()) {
            return;
        }
        setSimulationRunning(true);
        simulatedScenario.getProcessManager().createBatchProcesses();
    }
    public void stopSimulation() {
        setSimulationRunning(false);
    }

    public void addProcessListeners(Process process) {
        process.addOnCreateListener(this::notifyGuiOnCreatedProcess);
        process.addOnDispatchtListener(this::notifyGuiOnReadyProcess);
        process.addOnStartRunningListener(this::notifyGuiOnStartRunningProcess);
        process.addOnFinishListener(this::notifyGuiOnFinishedProcess);
        process.addOnUpdateListener(this::notifyGuiOnRunningProcess);
        //todo: notify on suspend and on resume.
    }

    private void notifyGuiOnRunningProcess(EventInfo eventInfo) {
        var process = ((ProcessEventInfo) eventInfo).getProcess();
        String pid = process.getPid();
        ProcessViewModel processViewModel = findProcessViewModel(process.getPid());
        if(processViewModel != null) {
            processViewModel.setState(process.getState());
        }
    }

    private void notifyGuiOnStartRunningProcess(EventInfo eventInfo) {
        var process = ((ProcessEventInfo) eventInfo).getProcess();
        String pid = process.getPid();
        ProcessViewModel processViewModel = findProcessViewModel(process.getPid());
        if(processViewModel != null) {
            processViewModel.setState(process.getState());
        }
    }

    private void notifyGuiOnReadyProcess(EventInfo eventInfo) {
        var process = ((ProcessEventInfo) eventInfo).getProcess();
        ProcessViewModel processViewModel = findProcessViewModel(process.getPid());
        if(processViewModel != null) {
            processViewModel.setState(process.getState());
        }
    }
    private void notifyGuiOnCreatedProcess(EventInfo eventInfo) {
        var process = ((ProcessEventInfo) eventInfo).getProcess();
        String pid = process.getPid();
        int priority = process.getPriority();
        Color selectedColor = Color.web(processScope.getColorProperty().getValue());
        ProcessViewModel processViewModel = new ProcessViewModel(pid,priority,selectedColor);
        processViewModel.setState(process.getState());
        processes.add(processViewModel);
    }
    private void notifyGuiOnFinishedProcess(EventInfo eventInfo) {
        var process = ((ProcessEventInfo) eventInfo).getProcess();
        String pid = process.getPid();
        ProcessViewModel processViewModel = findProcessViewModel(pid);
        if(processViewModel != null) {
            processViewModel.setState(process.getState());
        }
    }
    private ProcessViewModel findProcessViewModel(String pid) {
        for (ProcessViewModel processViewModel : processes) {
            if (processViewModel.pid().equals(pid)) {
                return processViewModel;
            }
        }
        return null;
    }
    public void runSimulation() {
        boolean hasMoreEvents = simulatedScenario.getSimulation().runClockAndProcessEvents();
        if(!hasMoreEvents) {
            stopSimulation();
        }
    }
    public BooleanProperty getSimulationRunningProperty() {
        return simulationRunning;
    }
    public boolean isSimulationRunning() {
        return simulationRunning.get();
    }
    private void setSimulationRunning(boolean running) {
        this.simulationRunning.set(running);
    }
}