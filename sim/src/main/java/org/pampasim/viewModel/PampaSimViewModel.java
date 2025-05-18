package org.pampasim.viewModel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.PampaSim;
import org.pampasim.SimulatedScenario;
import org.pampasim.core.*;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.core.utils.PidAllocator.Pid;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.entity.ProcessManager;
import org.pampasim.entity.Processor;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.resources.Process;
import org.pampasim.core.utils.GraphVisualizeable;
import org.pampasim.events.External.Arrival;
import org.pampasim.scopes.ProcessScope;
import org.pampasim.scopes.SchedulerDialogScope;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

public class PampaSimViewModel implements ViewModel {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimViewModel.class);
    @Getter
    private final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty genGraphs = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty simulationIsValidSetup = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty scenarioIsSaved = new SimpleBooleanProperty(true);
    private int graphNum = 0;
    @Getter
    private final ObservableMap<Pid, ProcessViewModel> processes = FXCollections.observableHashMap();
    @Getter
    private final ObservableMap<Long, ProcessViewModel> processesByCreationId = FXCollections.observableHashMap();

    @Getter
    @InjectScope
    private ProcessScope processScope;
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;

    public SimulatedScenario simulatedScenario;

    public PampaSimViewModel() {
        var templateSpec = Spec.loadSpec(Paths.get(
                Objects.requireNonNull(PampaSim.class.getResource("template.spec"))
                        .getPath())
        );
        simulatedScenario = new SimulatedScenario(templateSpec, spec -> {
            processesByCreationId.clear();
            processes.clear();
            var sim = PampaSim.fromSpec(spec);

            for (var entry : spec.getEventSchedule().entries()) {
                for (var event : entry.getValue()) {
                    if (event instanceof Arrival) {
                        handleProcessCreationEvent(event);
                    }
                }
            }

            var eventManager = sim.getEventManager();
            //eventManager.addSnooper(org.pampasim.events.ProcessCreationDataEvent.class,
            //        this::handleProcessCreationEvent);
            eventManager.addSnooper(org.pampasim.events.ProcessEvent.class,
                    this::handleProcessEvent);
            return sim;
        });
    }

    public void loadSpec(Path path) {
        var spec = Spec.loadSpec(path);
        LOGGER.debug("loaded {}", spec);
        if (spec == null) {
            throw new RuntimeException("couldn't load spec file at " + path);
        }

        LOGGER.info("loaded {}", path);
        simulatedScenario.setSpec(spec);
        simulatedScenario.resetToSpec();
        updateProps();
    }

    public void saveSpec(Path path) {
        var spec = simulatedScenario.getSpec();
        LOGGER.debug("saving {}", spec);
        simulatedScenario.saveSpec(path);
        LOGGER.info("saved to {}", path); // might've failed, report back if so FIXME
    }

    public void handleProcessCreationEvent(Event uncastEvent) {
        org.pampasim.events.ProcessCreationDataEvent event
                = (org.pampasim.events.ProcessCreationDataEvent)uncastEvent;
        Process.CreationData creationData = event.getCreationData();
        if (event instanceof Arrival) {
            ProcessViewModel pvm = new ProcessViewModel(creationData);
            Color processColor = simulatedScenario.getSpec().getColorMap()
                    .get(pvm.getCreationData().getCreationId());
            pvm.getColor().setValue(processColor);
            processesByCreationId.put(pvm.getCreationData().getCreationId(), pvm);
        }
    }
    public void handleProcessEvent(Event uncastEvent) {
        org.pampasim.events.ProcessEvent event = (org.pampasim.events.ProcessEvent)uncastEvent;
        Process proc = event.getProcess();
        ProcessViewModel procViewModel = processesByCreationId.get(proc.getCreationData().getCreationId());
        LOGGER.debug("got {}", event);
        switch (event) {
            case org.pampasim.events.Process.Ready e: {
                if (procViewModel == null) {
                    procViewModel = processesByCreationId.values()
                            .stream()
                            .filter(pvm -> {
                                var cdata = proc.getCreationData();
                                return !pvm.getInitialized().getValue() && (cdata == pvm.getCreationData());
                            }) // FIXME/WARN: we can't be sure the found pvm corresponds to the created process object
                            .findFirst()
                            .orElseThrow();
                    procViewModel.setPid(proc.getPid());
                    procViewModel.getInitialized().set(true);
                    processes.put(proc.getPid(), procViewModel);
                }
                procViewModel.getInitialized().set(true);
                procViewModel.setState(Process.State.NEW);
                updateProcessViewModel(procViewModel, proc);
                break;
            }
            default:
                if (procViewModel != null) {
                    updateProcessViewModel(procViewModel, proc);
                } else {
                    LOGGER.warn("got event {} without corresponding ProcessViewModel", event);
                }
                break;
        }
    }

    public void updateProcessViewModel(ProcessViewModel pvm, Process proc) {
        pvm.setState(proc.getState());
        pvm.getPriority().setValue(proc.getPriority());
        pvm.getCurrExecTime().setValue(proc.getCurrExecTime());
        pvm.getBurstTime().setValue(proc.getBurstTime());
    }

    public SchedulerDialogScope getSchedulerScope() {
        return schedulerDialogScope;
    }

    public void createNewProcess() {
        simulatedScenario.setSaved(false); // important line, must be set wherever we mutate spec
        var start = processScope.getStartTimeProperty().getValue();
        var duration = processScope.getDurationProperty().getValue();
        var priority = processScope.getPriorityProperty().getValue();
        var clr = processScope.getColorProperty().getValue();
        var creationData = new Process.CreationData(start, duration, priority);
        this.simulatedScenario.getSpec().addProcessArrival(creationData, Color.web(clr));
        resetSimulation();
        updateProps();
    }
    public void setSimulationScheduler() {
        simulatedScenario.setSaved(false); // important line, must be set wherever we mutate spec
        String schedulerName = schedulerDialogScope.getSchedulerNameProperty().getValue();
        Integer schedulerQuantum = schedulerDialogScope.getQuantumProperty().getValue();
        // TODO: It would be nice to disable the quantum input
        //  if it doesn't make sense for the currently selected algorithm
        simulatedScenario.getSpec()
                .setSchedulerInfo(
                        schedulerName,
                        Optional.ofNullable(schedulerQuantum));
        simulatedScenario.resetToSpec();
    }
    public void startSimulation() {
        if (!isValidSetup()) {
            throw new RuntimeException("tried to start a simulation without the correct setup");
        }
        setSimulationRunning(true);
    }

    public void resetSimulation() {
        simulatedScenario.resetToSpec();
    }

    public void stopSimulation() {
        setSimulationRunning(false);
    }

    private ProcessViewModel findProcessViewModel(PidAllocator.Pid pid) {
        return processes.get(pid);
    }
    public void runSimulation() {
        Simulation sim = simulatedScenario.getSimulation();
        if (sim.getState() == SimEntity.EntityState.Blocked) {
            sim.run();
        } else {
            sim.runUntilBlockedorIdle();
        }
        if(!sim.shouldRunNextTick()) {
            stopSimulation();
        }
        if (genGraphs.get()) {
            try { exportSimulationGraph(); }
            catch(Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
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
            String uniqueId = String.format("%04d", graphNum);
            viz.render(Format.SVG)
                    .toFile(new File("graph" + uniqueId + ".svg"));
            viz.render(Format.DOT)
                    .toFile(new File("graph" + uniqueId + ".dot"));
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
        scenarioIsSaved.set(simulatedScenario.isSaved());
    }
}
