package org.pampasim.viewModel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.*;
import org.pampasim.core.*;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.dialog.*;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.entity.ProcessManager;
import org.pampasim.entity.Processor;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.events.ProcessCreationDataEvent;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.dialog.MemoryConfigSelectionRecord;
import org.pampasim.memory.view.MemoryTabView;
import org.pampasim.memory.viewmodel.MemoryTabViewModel;
import org.pampasim.resources.Process;
import org.pampasim.core.utils.GraphVisualizeable;
import org.pampasim.resources.dialog.CreateProcessDialogService;
import org.pampasim.resources.dialog.CreateProcessRecord;
import org.pampasim.resources.dialog.ProcessMemoryInfoRecord;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;


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
    private final BooleanProperty memoryModulePresent = new SimpleBooleanProperty(false);


    //***** Dialog services *****//
    private final SelectSchedulerDialogService selectSchedulerDialogService = new SelectSchedulerDialogService();
    private final AddModuleDialogService addModuleDialogService = new AddModuleDialogService();
    private final CreateProcessDialogService createProcessDialogService = new CreateProcessDialogService();
    private final EditProcessDialogService editProcessDialogService = new EditProcessDialogService();

    private Map<PidAllocator.Pid, Map<Integer, Process.State>> asciiReportData = new HashMap<>();
    private int asciiReportClock = -1;

    public SimulatedScenario simulatedScenario;

    // Process States
    @Getter
    private final ObservableList<ProcessViewModel> allProcesses = FXCollections.observableArrayList();
    @Setter
    private TabPane tabPane;

    private MemoryTabViewModel memoryModule = null;

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

            for (var tick : spec.getEventSchedule().values()) {
                for (var event : tick) {
                    if (Objects.requireNonNull(event) instanceof ProcessCreationDataEvent e) {
                        var creationData = e.getCreationData();
                        ProcessViewModel vm = new ProcessViewModel(creationData.getCreationId());
                        vm.getColorProperty().set(spec.getColorMap().get(creationData.getCreationId()));
                        vm.setState(Process.State.NEW);
                        vm.getArrivalTick().set(creationData.getArrivalTick());
                        vm.getBurst().set(creationData.getDurationTicks());
                        vm.getPriority().set(creationData.getStartPriority());
                        allProcesses.add(vm);

                        //TODO: make adding module info part of the creation data
                        vm.addModuleInfoViewModel(new MemoryInfoViewModel());
                    }
                }
            }

            // FIXME: There likely is a more elegant solution than this
            MemoryManagement simMemoryModule = sim.getEntity(MemoryManagement.class);
            if (simMemoryModule != null) {
                simMemoryModule.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class,
                        this::handleProcessEvent);
            }
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
        syncWithSpec();
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
        var spec = simulatedScenario.getSpec();
        spec.addProcessArrival(creationData);
        spec.getColorMap().put(creationData.getCreationId(), Color.web(userProcess.color()));

        if (memoryModule != null) {
            ProcessMemoryInfoRecord memoryInfo = userProcess.memoryInfoRecord();
            var memoryCreationData = new ProcessMemoryInfo.CreationData(memoryInfo.processSize(),
                                                                        memoryInfo.fileBackedPages(),
                                                                        memoryInfo.memoryAccesses(),
                                                                        memoryInfo.modifiesPageFlags(),
                                                                        memoryInfo.loopAccess(),
                                                                        null);
            MemoryConfig.getProcessMemoryConfigs().put(creationData.getCreationId(), memoryCreationData);
        }
        // syncWithSpec(); // this is already called after this method in the only place that it is referenced
    }
    public void setSimulationScheduler(SchedulerSelectionRecord userSelection) {
        simulatedScenario.setSaved(false); // important line, must be set wherever we mutate spec
        simulatedScenario.getSpec()
                .setSchedulerInfo(
                        userSelection.schedulerName(),
                        Optional.of(userSelection.quantum()));
        syncWithSpec();
        updateProps();
    }

    public void setSimulationModules(AddModuleRecord userSelection) throws IOException {
        simulatedScenario.setSaved(false); // important line, must be set wherever we mutate spec
        // TODO: make the setup work with spec
        if (userSelection.module().equals("memory")) {

            reinitializeMemoryManagement();

            memoryModule = new MemoryTabViewModel(simulatedScenario.getSimulation().getEntity(MemoryManagement.class),
                                                    simulatedScenario.getSpec().getColorMap(),
                                                    allProcesses);


            memoryModulePresent.set(true);

            ViewTuple<MemoryTabView, MemoryTabViewModel> viewTuple = FluentViewLoader
                    .fxmlView(MemoryTabView.class)
                    .viewModel(memoryModule)
                    .load();

            Parent content = viewTuple.getView();

            Tab memoryTab = new Tab("Memória");
            memoryTab.setContent(content);

            ObservableList<Tab> tabs = tabPane.getTabs();
            if (tabs.size() > 1) {
                List<Tab> tabsToRemove = new ArrayList<>(tabs.subList(1, tabs.size()));
                tabs.removeAll(tabsToRemove);
            }

            tabPane.getTabs().add(memoryTab);
        }
    }

    public void startSimulation() {
        if (!isValidSetup()) {
            throw new RuntimeException("tried to start a simulation without the correct setup");
        }
        setSimulationRunning(true);
    }

    public void syncWithSpec() {
        allProcesses.clear();
        simulatedScenario.resetToSpec();
        if (memoryModulePresent.get()) {
            reinitializeMemoryManagement();
        }
    }

    public void stopSimulation() {
        setSimulationRunning(false);
    }

    public void runSimulation(boolean fullStep) {
        boolean blockedTick;
        Simulation sim = simulatedScenario.getSimulation();
        if (sim.getState() == SimEntity.EntityState.Blocked) {
            sim.run();
            blockedTick = true;
        } else {
            sim.runUntilBlockedorIdle();
            blockedTick = false;
            if (fullStep) {
                sim.run();
                blockedTick = true;
            }
        }

        { // Update ascii report
            if (blockedTick) {
                asciiReportClock = sim.getRealClock().getTick();
                for (ProcessViewModel pvm : allProcesses) {
                    PidAllocator.Pid pid = pvm.getPid().get();
                    if (pid == null) {
                        continue;
                    }
                    asciiReportData.compute(pid, (k, v) -> {
                        if (v == null) {
                            v = new HashMap<>();
                        }
                        v.put(asciiReportClock, pvm.getState());
                        return v;
                    });
                }
            }
        }

        if(!sim.shouldRunNextTick()) {
            int maxRealTick = sim.getRealClock().getTick();
            long maxPid = sim.getPidAllocator().assignPid().getId() - 1;
            int cellWidth = 1 + (int)Math.floor(Math.log10(maxRealTick));
            int pidWidth = 1 + (int)Math.floor(Math.log10(maxPid));
            final Map<Process.State, Character> stateChar = Map.of(
                    Process.State.NEW, 'n',
                    Process.State.READY, 'r',
                    Process.State.RUNNING, 'R',
                    Process.State.SCHEDULED, 'x',
                    Process.State.WAITING, 'w',
                    Process.State.IO_WAITING, 'i',
                    Process.State.IO_RUNNING, 'I',
                    Process.State.TERMINATED, 't'
            );

            Supplier<Stream<Integer>> range = () -> IntStream.rangeClosed(0, maxRealTick).boxed();
            String header = "pid " + " ".repeat(pidWidth) + " | clocks\n"
                    + "    " + " ".repeat(pidWidth) + " | "
                    + range.get().map(t -> String.format("%0"+cellWidth+"d", t))
                    .reduce((l,r) -> l.concat(" ").concat(r))
                    .orElseThrow()
                    + "\n";
            String asciiReportPrintout = "\n\tReport\n" + header +
                asciiReportData
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(procEntry -> {
                        var stateMap = procEntry.getValue();
                        return "pid " + procEntry.getKey() + " |"
                                + range.get().map(stateMap::get).map(state ->
                                String.format("%" + (cellWidth+1) + "s", (state != null)? stateChar.get(state) : '?')
                        ).reduce(String::concat).orElseThrow();
                    }).reduce((l,r) -> l.concat("\n").concat(r))
                    .orElse("couldn't generate report");
            LOGGER.log(Level.INFO, asciiReportPrintout);
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
        long id = proc.getCreationData().getCreationId();
        ProcessViewModel found = allProcesses.stream()
                .filter(pvm -> pvm.getCreationId() == id)
                .findFirst().orElse(null);

        if (found != null) {
            found.getPid().set(proc.getPid());
            found.setState(proc.getState());
            found.getPriority().set(proc.getPriority());
            found.getCurrExecTime().set(proc.getCurrExecTime());
            found.getBurstTime().set(proc.getBurstTime());
            double current = proc.getCurrExecTime();
            double total = proc.getCreationData().getDurationTicks();
            found.getProgress().set(current/total);
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

        selectSchedulerDialogService.setMemoryModulePresent(memoryModule != null);
        if (memoryModule != null) {
            List<String> pageReplacementAlgorithms = simulatedScenario.getSpec().listAvailablePageSubstitutionAlgorithms();
            selectSchedulerDialogService.showDialog(schedulers, pageReplacementAlgorithms).ifPresent(selection -> {
                MemoryConfigSelectionRecord mem = selection.memoryConfig();
                MemoryConfig.initialize(
                        mem.pageSize(),
                        mem.maxPagesPerProcess(),
                        mem.framesInRAM(),
                        mem.framesInSwap(),
                        mem.swapOperationLength(),
                        mem.workingSetWindow(),
                        mem.pageSubstitutionAlgorithm(),
                        mem.globalPageSubstitution(),
                        mem.anticipatedPageLoading(),
                        mem.prePagingRange(),
                        mem.variablePageAllocation(),
                        mem.variablePageAllocationTopThreshold(),
                        mem.variablePageAllocationBottomThreshold(),
                        mem.tlbEnabled(),
                        mem.tlbEntries()
                );
                setSimulationScheduler(selection);
            });
        } else {
            selectSchedulerDialogService.showDialog(schedulers).ifPresent(this::setSimulationScheduler);
        }

    }

    public void openAddModuleDialog() {
        List<String> modules = simulatedScenario.getSpec().listAvailableModules();
        addModuleDialogService.showDialog(modules).ifPresent(userSelection -> {
            try {
                setSimulationModules(userSelection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void openCreateProcessDialog() {
        createProcessDialogService.setMemoryModulePresent(memoryModule != null);
        createProcessDialogService.showDialog().ifPresent(this::createNewProcess);

        syncWithSpec();
    }

    public void openEditProcessDialog(ProcessViewModel editedProcessViewModel) {
//        int start = editedProcessViewModel.getCreationData().getArrivalTick();
//        int duration = editedProcessViewModel.getCreationData().getDurationTicks();
//        int priority = editedProcessViewModel.getCreationData().getStartPriority();
//        ObjectProperty<Color> color = editedProcessViewModel.getColorProperty();
//        Optional<EditProcessRecord> result = editProcessDialogService.showDialog(start, duration, priority, color);
    }

    private void reinitializeMemoryManagement() {
        // Cria a entidade MemoryManagement na simulação
        new MemoryManagement((SimulationBase) simulatedScenario.getSimulation());

        // Obtém a instância da entidade recém-criada
        MemoryManagement simMemoryModule = simulatedScenario.getSimulation().getEntity(MemoryManagement.class);

        if (simMemoryModule != null) {
            // Garante que eventos de processo da memória sejam tratados
            simMemoryModule.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class, this::handleProcessEvent);
        }

        // Atualiza o MemoryTabViewModel, se já estiver instanciado
        if (memoryModule != null) {
            memoryModule.setMemoryManagement(simMemoryModule, simulatedScenario.getSpec().getColorMap());
            memoryModule.refreshFrameList();
        }
    }

}
