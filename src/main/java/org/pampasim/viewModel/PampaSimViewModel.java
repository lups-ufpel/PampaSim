package org.pampasim.viewModel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pampasim.SimCore.*;
import org.pampasim.SimCore.events.ProcessArrival;
import org.pampasim.SimCore.events.ProcessEvent;
import org.pampasim.SimEntity.ProcessManager;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimEntity.Schedulers.Scheduler;
import org.pampasim.SimResources.Process;
import org.pampasim.Utils.GraphVisualizeable;
import org.pampasim.scopes.ProcessScope;
import org.pampasim.scopes.SchedulerDialogScope;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Collectors;

public class PampaSimViewModel implements ViewModel {
    @Getter
    private final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty genGraphs = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty simulationIsValidSetup = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty simulationIsFresh = new SimpleBooleanProperty(true);
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
    }

    public void loadSpec(URL url) {
        simulatedScenario.loadSpec(url);
        var spec = simulatedScenario.getSpec();
        System.out.println("scheduler " + simulatedScenario.getSimulation().getEntity(Scheduler.class));
        System.out.println("got " + spec);
        if (spec != null) {
            for (var events : spec.getEventSchedule().values()) {
                for (var ev : events.stream().filter(e -> e instanceof ProcessEvent).map(e -> (ProcessEvent)e).toList()) {
                    Process p = ev.getProcess();
                    System.out.println("registering proc " + p);
                    // treat them like the create process button would
                    addProcessListeners(p);
                    p.notifyListenersOnCreate();
                }
            }
        }
        updateProps();
    }

    public SchedulerDialogScope getSchedulerScope() {
        return schedulerDialogScope;
    }

    public void createNewProcess() {
        var start = processScope.getStartTimeProperty().getValue();
        var duration = processScope.getDurationProperty().getValue();
        var priority = processScope.getPriorityProperty().getValue();
        Process newProcess = new Process(priority,duration,start,
                simulatedScenario.simulation.getPidAllocator().assignPid() // assigns a unique Pid within the simulation to the Process
                );
        var newEvent = new ProcessArrival(null, newProcess);
        simulatedScenario.getSimulation().scheduleToClock(start, newEvent);
        simulatedScenario.getSpec().addProcessArrival(newProcess); // commit to spec so we may save it later
        // FIXME: since we are updating the spec, we may as well make the start of the sim
        // load from this built up spec, no?
        this.addProcessListeners(newProcess);
        newProcess.notifyListenersOnCreate();
        updateProps();
    }
    public void setSimulationScheduler() {
        String schedulerName = schedulerDialogScope.getSchedulerNameProperty().getValue();
        Integer schedulerQuantum = schedulerDialogScope.getQuantumProperty().getValue();
        // TODO: It would be nice to disable the quantum input
        //  if it doesn't make sense for the currently selected algorithm
        simulatedScenario.getSpec()
                .setSchedulerInfo(
                        schedulerName,
                        Optional.ofNullable(schedulerQuantum));
        // TODO/FIXME: need to figure out how to apply it immediately here
    }
    public void startSimulation() {
        if (!isValidSetup()) {
            throw new RuntimeException("tried to start a simulation without the correct setup");
        }
        setSimulationRunning(true);
    }

    public void resetSimulation() {
        ChoiceDialog<String> confirmationDialog = new ChoiceDialog<>("No", "Yes", "No");

        if (confirmationDialog.showAndWait().orElse("No").equals("Yes")) {
            simulatedScenario.resetToSpec();
        }
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
        var spec = simulatedScenario.getSpec();
        if (spec != null) {
            Color c = spec.getColorMap().getOrDefault(process, null);
            if (c != null) {
                selectedColor = c;
            }
        }
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
        Simulation sim = simulatedScenario.getSimulation();
        sim.run();
        if(!sim.hasPendingEvents()) {
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
        if (sim != null) {
            var graph = ((GraphVisualizeable)sim).exportGraph();
            Graphviz viz = Graphviz.fromGraph(graph);
            viz.render(Format.SVG)
                    .toFile(new File("graph" + graphNum + ".svg"));
            viz.render(Format.DOT)
                    .toFile(new File("graph" + graphNum + ".dot"));
        }
        graphNum++;
    }
    public boolean isValidSetup() {
        // FIXME / TODO: this can be made more thorough by analysing if there are any unhandled events
        return simulatedScenario.getSimulation().getEntity(Scheduler.class) != null
            && simulatedScenario.getSimulation().getEntity(Processor.class) != null
            && simulatedScenario.getSimulation().getEntity(ProcessManager.class) != null;
    }

    public void updateProps() {
        simulationIsValidSetup.set(isValidSetup());
        simulationIsFresh.set(simulatedScenario.getSimulation().isFresh());
    }
}
