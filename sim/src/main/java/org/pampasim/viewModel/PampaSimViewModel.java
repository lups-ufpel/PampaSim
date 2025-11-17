package org.pampasim.viewModel;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
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
import org.pampasim.memory.viewmodel.MemoryStatisticsViewModel;
import org.pampasim.memory.viewmodel.MemoryTabViewModel;
import org.pampasim.resources.Process;
import org.pampasim.core.utils.GraphVisualizeable;
import org.pampasim.resources.dialog.CreateProcessDialogService;
import org.pampasim.resources.dialog.CreateProcessRecord;
import org.pampasim.resources.dialog.ProcessMemoryInfoRecord;
import org.pampasim.dialog.AddSpecOrModulesDialogService;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class PampaSimViewModel implements ViewModel {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimViewModel.class);
    @Getter
    private final BooleanProperty simulationRunning = new SimpleBooleanProperty(false);
    //@Getter
    //private final BooleanProperty genGraphs = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty simulationIsValidSetup = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty scenarioIsSaved = new SimpleBooleanProperty(true);
    private int graphNum = 0;

    @Getter
    private final BooleanProperty memoryModulePresent = new SimpleBooleanProperty(false);

    @Getter
    private final SimulationStatisticsViewModel simulationStatisticsViewModel = new SimulationStatisticsViewModel();;


    //***** Dialog services *****//
    private final SettingsDialogService settingsDialogService = new SettingsDialogService();
    private final AddModulesDialogService addModulesDialogService = new AddModulesDialogService();
    private final CreateProcessDialogService createProcessDialogService = new CreateProcessDialogService();
    private final EditProcessDialogService editProcessDialogService = new EditProcessDialogService();
    private final AddSpecOrModulesDialogService addSpecOrModulesDialogService = new AddSpecOrModulesDialogService();

    @Getter
    private ObservableMap<PidAllocator.Pid, ObservableMap<Integer, Process.State>> ganttData = FXCollections.observableHashMap();
    private final Map<Process.State, Character> stateChar = Map.of(
            Process.State.NEW, 'n',
            Process.State.READY, 'r',
            Process.State.RUNNING, 'R',
            Process.State.SCHEDULED, 'x',
            Process.State.WAITING, 'w',
            Process.State.IO_WAITING, 'i',
            Process.State.IO_RUNNING, 'I',
            Process.State.TERMINATED, 't'
    );
    private int asciiReportClock = -1;

    public SimulatedScenario simulatedScenario;

    // Process States
    @Getter
    private final ObservableList<ProcessViewModel> allProcesses = FXCollections.observableArrayList();
    @Setter
    private TabPane tabPane;

    private MemoryTabViewModel memoryModule = null;

    public PampaSimViewModel() {
        var templateSpecStream = PampaSim.class.getResourceAsStream("template.spec");
        var templateSpec = Spec.loadSpec(templateSpecStream);
        simulatedScenario = new SimulatedScenario(templateSpec, spec -> {
            var sim = PampaSim.fromSpec(spec);
            var eventManager = sim.getEventManager();
            eventManager.addSnooper(org.pampasim.events.ProcessEvent.class,
                    this::handleProcessEvent);
            if (memoryModulePresent.get()) {
                reinitializeMemoryManagement(sim);
            }
            MemoryManagement simMemoryModule = sim.getEntity(MemoryManagement.class);

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
                        if (simMemoryModule != null) {
                            vm.addModuleInfoViewModel(new MemoryInfoViewModel(MemoryConfig.getProcessMemoryConfigs().get(creationData.getCreationId()),
                                                                              MemoryConfig.getWorkingSetWindow(),
                                                                              MemoryConfig.getMaxPagesPerProcess()));
                        }
                    }
                }
            }

            simulationStatisticsViewModel.updateStatistics(sim, allProcesses);

            // FIXME: There likely is a more elegant solution than this
            if (simMemoryModule != null) {
                simMemoryModule.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class,
                        this::handleProcessEvent);
                simulationStatisticsViewModel.getModuleStatisticsViewModel(MemoryStatisticsViewModel.class).updateStatistics(simMemoryModule, allProcesses);

            }
            return sim;
        });
    }

    public void loadSpec(Path path) {
        try {
            var spec = Spec.loadSpec(new FileInputStream(path.toString()));
            LOGGER.debug("loaded {}", spec);
            if (spec == null) {
                throw new RuntimeException("couldn't load spec file at " + path);
            }

            LOGGER.info("loaded {}", path);
            simulatedScenario.setSpec(spec);
            syncWithSpec();
            updateProps();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    public void setSimulationModules(AddModulesRecord userSelection) throws IOException {
        simulatedScenario.setSaved(false); // important line, must be set wherever we mutate spec

        if (userSelection.modules().getFirst().equals("memory")) { // FIXME: multiple modules
            reinitializeMemoryManagement((SimulationBase) simulatedScenario.getSimulation());
            MemoryStatisticsViewModel memoryStatisticsViewModel = new MemoryStatisticsViewModel();
            simulationStatisticsViewModel.addModuleStatisticsViewModel(memoryStatisticsViewModel);

            memoryModule = new MemoryTabViewModel(
                    simulatedScenario.getSimulation().getEntity(MemoryManagement.class),
                    simulatedScenario.getSpec().getColorMap(),
                    allProcesses,
                    memoryStatisticsViewModel
            );

            memoryModulePresent.set(true);

            ViewTuple<MemoryTabView, MemoryTabViewModel> viewTuple = FluentViewLoader
                    .fxmlView(MemoryTabView.class)
                    .viewModel(memoryModule)
                    .load();

            Parent content = viewTuple.getView();

            FontIcon icon = new FontIcon(BootstrapIcons.BOX_ARROW_UP_RIGHT);
            icon.setIconSize(14);

            // Create the memory tab with a pop-out button in the header
            Tab memoryTab = new Tab();
            HBox header = new HBox(5);
            header.setAlignment(Pos.CENTER_LEFT); // center vertically
            Label title = new Label("Memória");
            Button popOutBtn = getPopoutButton(icon, memoryTab);

            header.getChildren().addAll(title, popOutBtn);
            memoryTab.setGraphic(header);
            memoryTab.setContent(content);
            memoryTab.setClosable(false);

            ObservableList<Tab> tabs = tabPane.getTabs();
            if (tabs.size() > 1) {
                List<Tab> tabsToRemove = new ArrayList<>(tabs.subList(1, tabs.size()));
                tabs.removeAll(tabsToRemove);
            }

            tabPane.getTabs().add(memoryTab);
        }
    }

    private Button getPopoutButton(FontIcon icon, Tab memoryTab) {
        Button popOutBtn = new Button();
        popOutBtn.setGraphic(icon);
        popOutBtn.setFocusTraversable(false);

        popOutBtn.setOnAction(e -> {
            if (memoryTab.getContent() == null) return;

            Parent poppedContent = (Parent) memoryTab.getContent();
            memoryTab.setContent(null);

            Stage popOutStage = new Stage();
            popOutStage.setTitle("Memória");

            BorderPane layout = new BorderPane(poppedContent);
            Scene popOutScene = new Scene(layout, 800, 600);
            popOutStage.setScene(popOutScene);
            popOutStage.initOwner(tabPane.getScene().getWindow());

            popOutStage.setOnCloseRequest(event -> {
                memoryTab.setContent(poppedContent);
            });

            popOutStage.show();
        });
        return popOutBtn;
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
                    ganttData.compute(pid, (k, v) -> {
                        if (v == null) {
                            v = FXCollections.observableHashMap();
                        }
                        v.put(asciiReportClock, pvm.getState());
                        return v;
                    });
                }
            }
        }

        if(!sim.shouldRunNextTick()) {
            generateASCIIReport(sim);
            generateCSVReport(sim);

            stopSimulation();
            this.asciiReportClock = 0;
            this.ganttData.clear();
        }
        /*
        if (genGraphs.get()) {
            try { exportSimulationGraph(); }
            catch(Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
        */
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
            found.getEndTime().set(proc.getEndTime());
        }

        // updating the wait time for the processes in the scheduler queue
        if (event instanceof org.pampasim.events.Process.Run || event instanceof org.pampasim.events.Process.RunPaused) {
            Queue<Process> processQueue = (Queue<Process>) simulatedScenario.getSimulation()
                    .getEntity(Scheduler.class)
                    .getProcessQueue();

            for (Process queuedProc : processQueue) {
                long queuedId = queuedProc.getCreationData().getCreationId();
                allProcesses.stream()
                        .filter(pvm -> pvm.getCreationId() == queuedId)
                        .findFirst()
                        .ifPresent(pvm -> pvm.getReadyWaitingTime().set(queuedProc.getWaitTime()));
            }
        }
        simulationStatisticsViewModel.updateStatistics((SimulationBase) simulatedScenario.getSimulation(), allProcesses);

    }

    private void generateCSVReport(Simulation sim) {
        int maxRealTick = sim.getRealClock().getTick();

        Supplier<Stream<Integer>> range = () -> IntStream.rangeClosed(0, maxRealTick).boxed();
        var headerBuilder = new StringBuilder("pid");
        for (var x = 0; x <= maxRealTick; x++) {
            headerBuilder.append(",").append(x);
        }
        headerBuilder.append("\n");

        String csv = headerBuilder.toString() + ganttData
            .entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(procEntry -> {
                        var stateMap = procEntry.getValue();
                        var rowBuilder = new StringBuilder()
                                .append(procEntry.getKey()); // pid
                        range.get().map(stateMap::get).forEach(state -> {
                            rowBuilder.append(",").append((state != null) ? stateChar.get(state) : '?');
                        });
                        return rowBuilder.append("\n").toString();
                    }).reduce((l,r) -> l.concat("\n").concat(r))
                .orElse("couldn't generate csv report");
        try {
            Files.writeString(Paths.get("gantt.csv"), csv, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOGGER.error("couldn't write CSV simulation report!", e);
        }
    }

    private void generateASCIIReport(Simulation sim) {
        int maxRealTick = sim.getRealClock().getTick();
        long maxPid = sim.getPidAllocator().assignPid().getId() - 1;
        int cellWidth = 1 + (int)Math.floor(Math.log10(maxRealTick));
        int pidWidth = 1 + (int)Math.floor(Math.log10(maxPid));

        Supplier<Stream<Integer>> range = () -> IntStream.rangeClosed(0, maxRealTick).boxed();
        String header = "pid " + " ".repeat(pidWidth) + " | clocks\n"
                + "    " + " ".repeat(pidWidth) + " | "
                + range.get().map(t -> String.format("%0"+cellWidth+"d", t))
                .reduce((l,r) -> l.concat(" ").concat(r))
                .orElseThrow()
                + "\n";
        String asciiReportPrintout = "\n\tReport\n" + header +
            ganttData
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
    public void openSettingsDialog() {
        List<String> schedulers = simulatedScenario.getSpec().listAvailableSchedulers();

        settingsDialogService.setMemoryModulePresent(memoryModule != null);
        if (memoryModule != null) {
            List<String> pageReplacementAlgorithms = simulatedScenario.getSpec().listAvailablePageSubstitutionAlgorithms();
            settingsDialogService.showDialog(schedulers, pageReplacementAlgorithms).ifPresent(selection -> {
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
            settingsDialogService.showDialog(schedulers).ifPresent(this::setSimulationScheduler);
        }

    }

    ///  returns true if the spec is already loadable with no further changes.
    public boolean openAddSpecOrModuleDialog() {
        List<String> modules = simulatedScenario.getSpec().listAvailableModules();
        AtomicBoolean done = new AtomicBoolean(false);
        addSpecOrModulesDialogService.showDialog(modules).ifPresent(userSelection -> {
                userSelection.modulesRecord().ifPresent(mods -> {
                    try {
                        setSimulationModules(mods);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                userSelection.specPath().ifPresent(specPath -> {
                    loadSpec(Paths.get(specPath));
                    done.set(true);
                });
        });
        return done.get();
    }

    public void openAddModuleDialog() {
        List<String> modules = simulatedScenario.getSpec().listAvailableModules();
        addModulesDialogService.showDialog(modules).ifPresent(userSelection -> {
            try {
                setSimulationModules(userSelection);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void openCreateProcessDialog() {
        createProcessDialogService.setMemoryModulePresent(memoryModule != null);
        createProcessDialogService.setMemoryPageSize(MemoryConfig.getPageSize());
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

    private void reinitializeMemoryManagement(SimulationBase simulationBase) {

        simulationBase.removeModule(MemoryManagement.class);

        new MemoryManagement(simulationBase);

        MemoryManagement simMemoryModule = simulationBase.getEntity(MemoryManagement.class);

        if (simMemoryModule != null) {
            simMemoryModule.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class, this::handleProcessEvent);
        }

        if (memoryModule != null) {
            memoryModule.setMemoryManagement(simMemoryModule, simulatedScenario.getSpec().getColorMap());
            memoryModule.refreshFrameList();
        }
    }

}
