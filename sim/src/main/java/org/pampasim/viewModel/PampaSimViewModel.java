package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.*;
import org.pampasim.core.*;
import org.pampasim.core.events.Event;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.entity.ProcessManager;
import org.pampasim.entity.Processor;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.resources.Process;
import org.pampasim.core.utils.GraphVisualizeable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    //***** Dialog services *****//
    private final SelectSchedulerDialogService selectSchedulerDialogService = new SelectSchedulerDialogService();
    private final CreateProcessDialogService createProcessDialogService = new CreateProcessDialogService();
    private final EditProcessDialogService editProcessDialogService = new EditProcessDialogService();

    public SimulatedScenario simulatedScenario;

    // Process States
    @Getter
    private final ObservableList<ProcessViewModel> allProcesses = FXCollections.observableArrayList();

    private FilteredList<ProcessViewModel> newProcesses;

    public PampaSimViewModel() {
        var templateSpec = Spec.loadSpec(Paths.get(
                Objects.requireNonNull(PampaSim.class.getResource("template.spec"))
                        .getPath())
        );
        simulatedScenario = new SimulatedScenario(templateSpec, spec -> {
            var sim = PampaSim.fromSpec(spec);
            var eventManager = sim.getEventManager();
            eventManager.addSnooper(org.pampasim.events.ProcessEvent.class,
                    this::handleProcessEvent);
            return sim;
        } );
        allProcesses.addListener((ListChangeListener<ProcessViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewModel novo : change.getAddedSubList()) {
                        attachStateListener(novo);
                        System.out.println("Adicionado: " + novo);
                        var creationData = novo.getCreationData();
                        simulatedScenario.setSaved(false);
                        this.simulatedScenario.getSpec().addProcessArrival(creationData);
                        resetSimulation();
                        updateProps();
                    }
                }
                if (change.wasRemoved()) {
                    for (ProcessViewModel removido : change.getRemoved()) {
                        System.out.println("Removido: " + removido);
                    }
                }
            }
        });
        newProcesses = new FilteredList<>(allProcesses,
                p -> p.getState() == Process.State.NEW);
    }
    private void attachStateListener(ProcessViewModel pvm) {
        pvm.stateProperty().addListener((obs, oldState, newState) -> {
            //refresh the filter
            System.out.println("worked!");
            newProcesses.setPredicate(p -> p.getState() == Process.State.NEW);
            System.out.println(newProcesses.toString());
        } );
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

    public void createNewProcess(CreateProcessRecord userProcess) {
        var creationData = new Process.CreationData(userProcess.start(), userProcess.duration(), userProcess.priority());
        ProcessViewModel vm = new ProcessViewModel(creationData);
        vm.getColorProperty().set(Color.web(userProcess.color()));
        vm.setState(Process.State.NEW);
        allProcesses.add(vm);
    }
    public void setSimulationScheduler(SchedulerSelectionRecord userSelection) {
        simulatedScenario.setSaved(false); // important line, must be set wherever we mutate spec
        simulatedScenario.getSpec()
                .setSchedulerInfo(
                        userSelection.schedulerName(),
                        Optional.of(userSelection.quantum()));
        simulatedScenario.resetToSpec();
        updateProps();
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
    public void handleProcessEvent(Event uncastEvent) {
        org.pampasim.events.ProcessEvent event = (org.pampasim.events.ProcessEvent)uncastEvent;
        Process proc = event.getProcess();
        System.out.println("Proc Creation Id: " + proc.getCreationData().getCreationId());
        // GET THE VIEWMODEL HERE
        LOGGER.debug("got {}", event);
        switch (event) {
            case org.pampasim.events.Process.Ready e: {
                long id = proc.getCreationData().getCreationId();
                ProcessViewModel found = allProcesses.stream()
                                .filter(pvm -> pvm.getCreationData().getCreationId() == id)
                                        .findFirst().orElse(null);
                if(found == null) {
                    System.out.println("something wrong");
                }
                found.setState(Process.State.READY);
                System.out.println(newProcesses.toString());
                break;
            }
            default:
                System.out.println("handleProcessEvent caiu no default");
                System.out.println("proc state:" + proc.getState());
                break;
        }
    }
    private void setSimulationRunning(boolean running) {
        this.simulationRunning.set(running);
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
    public void openSelectSchedulerDialog() {
        List<String> schedulers = simulatedScenario.getSpec().listAvailableSchedulers();
        selectSchedulerDialogService.showDialog(schedulers).ifPresent(this::setSimulationScheduler);
    }
    public void openCreateProcessDialog() {
        createProcessDialogService.showDialog().ifPresent(this::createNewProcess);
    }

    public void openEditProcessDialog(ProcessViewModel editedProcessViewModel) {
        int start = editedProcessViewModel.getCreationData().getArrivalTick();
        int duration = editedProcessViewModel.getCreationData().getDurationTicks();
        int priority = editedProcessViewModel.getCreationData().getStartPriority();
        ObjectProperty<Color> color = editedProcessViewModel.getColorProperty();
        Optional<EditProcessRecord> result = editProcessDialogService.showDialog(start, duration, priority, color);
    }
    public FilteredList<ProcessViewModel> getNewProcesses() {
        return newProcesses;
    }
}
