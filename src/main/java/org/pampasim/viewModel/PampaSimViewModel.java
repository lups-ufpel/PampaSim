package org.pampasim.viewModel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import guru.nidi.graphviz.engine.Engine;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.SimCore.EventInfo;
import org.pampasim.SimCore.ProcessEventInfo;
import org.pampasim.SimCore.SimulatedScenario;
import org.pampasim.SimEntity.ProcessManager;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimEntity.Scheduler;
import org.pampasim.SimResources.Process;
import org.pampasim.SimResources.ProcessorCore;
import org.pampasim.Utils.GraphVisualizeable;
import org.pampasim.scopes.ProcessScope;
import org.pampasim.scopes.SchedulerDialogScope;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class PampaSimViewModel implements ViewModel {
    @Getter
    private final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty genGraphs = new SimpleBooleanProperty(false);
    private int graphNum = 0;
    @Getter
    private final ObservableList<ProcessViewModel> processes = FXCollections.observableArrayList();

    @Getter
    @InjectScope
    private ProcessScope processScope;
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;

    public SimulatedScenario simulatedScenario;

    public PampaSimViewModel() {
        simulatedScenario = new SimulatedScenario();
        ProcessManager kernel = new ProcessManager(simulatedScenario.getSimulation());
        // These were getting dropped at the end of this scope, why?
        // UPDATE: now i know why. this doesn't make me feel any better about this.
        ProcessorCore core = new ProcessorCore(100);
        Processor processor = new Processor(simulatedScenario.getSimulation(), core);
        simulatedScenario.setProcessManager(kernel);
    }

    public SchedulerDialogScope getSchedulerScope() {
        return schedulerDialogScope;
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
        process.addOnDispatchListener(this::notifyGuiOnReadyProcess);
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
        if (genGraphs.get()) {
            try { exportSimulationGraph(); }
            catch(Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }
    public boolean isSimulationRunning() {
        return simulationRunning.get();
    }
    private void setSimulationRunning(boolean running) {
        this.simulationRunning.set(running);
    }
    public void setGenGraphs(boolean val) {
        this.genGraphs.set(val);
    }
    public void exportSimulationGraph() throws IOException {
        var sim = this.simulatedScenario.getSimulation();
        if (sim instanceof GraphVisualizeable) {
            var graph = ((GraphVisualizeable)sim).exportGraph();
            Graphviz viz = Graphviz.fromGraph(graph);
            viz.render(Format.SVG)
                    .toFile(new File("graph" + graphNum + ".svg"));
            viz.render(Format.DOT)
                    .toFile(new File("graph" + graphNum + ".dot"));
        }
        graphNum++;
    }
}
