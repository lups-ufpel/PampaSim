package org.pampasim.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.ViewListBinder;
import org.pampasim.resources.Process;
import org.pampasim.resources.view.ProcessView;
import org.pampasim.viewModel.PampaSimViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public class PampaSimView implements FxmlView<PampaSimViewModel>, Initializable {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimView.class);
    @InjectViewModel
    private PampaSimViewModel pampaSimViewModel;
    @FXML
    public SplitPane simPane;
    @FXML
    public ScrollPane masterScrollPane;
    @FXML
    public ProgressBar ProcessProgress;
    @FXML
    public HBox RunningList;
    @FXML
    public HBox NewList;
    @FXML
    public HBox ReadyList;
    @FXML
    public HBox WaitingList;
    @FXML
    public HBox FinishedList;
    @FXML
    public Button addProcessBtn;
    @FXML
    public Button runBtn;
    @FXML
    public Button resetBtn;
    @FXML
    public Button stopBtn;
    @FXML
    public Button loadSpecBtn;
    @FXML
    public Button configBtn;
    @FXML
    public Button saveSpecBtn;
    @FXML
    public TableView<ProcessViewModel> procTable;
    @FXML
    public TableColumn<ProcessViewModel, Color> colorCol;
    @FXML
    public TableColumn<ProcessViewModel, String> pidCol;
    @FXML
    public TableColumn<ProcessViewModel, Process.State> stateCol;
    @FXML
    public TableColumn<ProcessViewModel, Integer> arrivalCol;
    @FXML
    public TableColumn<ProcessViewModel, Integer> priorityCol;
    @FXML
    public TableColumn<ProcessViewModel, Integer> burstCol;
    @FXML
    public TableColumn<ProcessViewModel, Double> progressCol;
    @FXML
    public TabPane moduleTabPane;
    @FXML
    public Button stepBtn;
    @FXML
    public Button statisticsBtn;
    @FXML
    public Button addModuleBtn;
    public record GanttLine(PidAllocator.Pid pid, ObservableMap<Integer, Process.State> stateMap) {};
    @FXML
    public TableView<ObservableValue<GanttLine>> ganttChart;
    @FXML
    public TableColumn<ObservableValue<GanttLine>, PidAllocator.Pid> ganttPidCol;
    private ObservableList<ObservableValue<GanttLine>> ganttLines;

    private javafx.animation.Timeline animation;
    private ProcessViewModel editedProcessViewModel = null;

    @FXML
    public Slider stepFreqSlider;
    @FXML
    public Label stepFreqReadOut;
    public Instant lastStepT;

    @FXML
    public void onStartSimulation(ActionEvent actionEvent) {
        pampaSimViewModel.startSimulation();
        if(pampaSimViewModel.getSimulationRunning().get()) {
            LOGGER.debug("started animation");
            animation.play();
        }
        lastStepT = java.time.Instant.now();
    }
    @FXML
    public void onResetSimulation(ActionEvent actionEvent) {
        pampaSimViewModel.syncWithSpec();
    }
    // somewhat misleading name, also called when the stop button is clicked
    @FXML
    public void onFinishSimulation(ActionEvent actionEvent) {
        animation.pause();
        pampaSimViewModel.stopSimulation();
    }
    @FXML
    public void createProcess(ActionEvent actionEvent) {
        pampaSimViewModel.openCreateProcessDialog();
    }
    @FXML
    public void onSettings(ActionEvent actionEvent) {
        pampaSimViewModel.openSettingsDialog();
    }
    @FXML
    public void onSelectModule(ActionEvent actionEvent) {
        pampaSimViewModel.openAddModuleDialog();
    }
    @FXML
    public void loadSpec() {
        if (! pampaSimViewModel.getScenarioIsSaved().get()) {
            Alert confirmOverwriteAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmOverwriteAlert.setTitle("Confirmar operação?");
            confirmOverwriteAlert.setHeaderText("Essa operação irá substituir o cenário atual!");
            confirmOverwriteAlert.setContentText("Salve as suas alterações para evitar perda de dados. Continuar mesmo assim?");
            var result = confirmOverwriteAlert.showAndWait();
            if (result.isPresent() && (! result.get().equals(ButtonType.OK))) {
                return; // early return, aborting the op.
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open specification file");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            pampaSimViewModel.loadSpec(Paths.get(file.getPath()));
        }
    }

    @FXML
    public void saveSpec() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open specification file");
        File file = fileChooser.showSaveDialog(null);
        pampaSimViewModel.saveSpec(Paths.get(file.getPath()));
        pampaSimViewModel.updateProps();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pampaSimViewModel.setTabPane(moduleTabPane); // FIXME: tight coupling

        ViewListBinder.bind(
                Map.of(
                        Process.State.NEW, NewList,
                        Process.State.READY, ReadyList,
                        Process.State.RUNNING, RunningList,
                        Process.State.SCHEDULED, WaitingList,
                        Process.State.IO_WAITING, WaitingList,
                        Process.State.IO_RUNNING, WaitingList,
                        Process.State.TERMINATED, FinishedList),
                ProcessProgress,
                ProcessViewModel::getProgress,
                pampaSimViewModel.getAllProcesses(),
                ViewListBinder.mvvmfxFxmlFactory(ProcessView.class),
                Process.State.RUNNING
        );

        var keyframe = new KeyFrame(Duration.seconds(1), e -> pampaSimViewModel.runSimulation(false));
        this.animation = new javafx.animation.Timeline(keyframe);
        this.animation.setDelay(Duration.ZERO);
        this.animation.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        bindTimeLineProperty();
        bindStepFreqSlider();
        bindButtons();

        pampaSimViewModel.updateProps();
        updateAnimationRate(); // Just to populate the step freq read out

        // Bind the TableView's items to the ViewModel's ObservableList
        procTable.setItems(pampaSimViewModel.getAllProcesses());

        // Configure each TableColumn’s cellValueFactory to use the corresponding property:
        colorCol.setCellValueFactory(cellData -> cellData.getValue().getColorProperty());
        // https://stackoverflow.com/a/39415402
        colorCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) { setText(null); setStyle(""); }
                else {
                    setText(item.toString());
                    setStyle("-fx-background-color: #" + item.toString().substring(2));
                }
            }
        });
        pidCol.setCellValueFactory(cellData -> cellData.getValue().getPid().map(Object::toString));
        stateCol.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        arrivalCol.setCellValueFactory(cellData -> cellData.getValue().getArrivalTick());
        priorityCol.setCellValueFactory(cellData -> cellData.getValue().getPriority());
        burstCol.setCellValueFactory(cellData -> cellData.getValue().getPriority());
        progressCol.setCellValueFactory(p -> {
            return p.getValue().getProgress();
            });

        progressCol.setCellFactory(column -> new TableCell<>() {
            private final ProgressBar progressBar = new ProgressBar(); // progress bar
            private final Label progressLabel = new Label(); // text overlay
            private final StackPane stackPane = new StackPane(); // container to stack the text over the progress bar

            {
                // style settings
                progressBar.setMaxWidth(Double.MAX_VALUE);
                progressBar.setPrefHeight(20);
                progressLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold;");
                stackPane.getChildren().addAll(progressBar, progressLabel);
            }

            @Override
            protected void updateItem(Double progress, boolean empty) {
                super.updateItem(progress, empty);

                if (empty || progress == null) {
                    setGraphic(null);
                } else {
                    progressBar.setProgress(progress);
                    ProcessViewModel process = getTableView().getItems().get(getIndex());
                    int current = process.getCurrExecTime().getValue();
                    int total = process.getBurst().getValue();

                    progressLabel.setText(current + "/" + total);
                    setGraphic(stackPane);
                }
            }
        });

        ganttLines = FXCollections.observableArrayList();
        bindGanttChartObservable();
    }

    private void bindButtons() {
        addProcessBtn.disableProperty().bind(
                pampaSimViewModel
                        .getSimulationIsValidSetup().not()
                        .or(pampaSimViewModel.getSimulationRunning())
        );
        runBtn.disableProperty().bind(
                pampaSimViewModel
                        .getSimulationIsValidSetup().not()
                        .or(pampaSimViewModel.getSimulationRunning())
        );
        stepBtn.disableProperty().bind(
                pampaSimViewModel
                        .getSimulationIsValidSetup().not()
                        .or(pampaSimViewModel.getSimulationRunning())
        );
        statisticsBtn.disableProperty().bind(
                pampaSimViewModel
                        .getSimulationIsValidSetup().not()
        );

        addModuleBtn.disableProperty().bind(
                pampaSimViewModel
                        .getSimulationIsValidSetup()
                        .or(pampaSimViewModel.getSimulationRunning())
                        .or(pampaSimViewModel.getMemoryModulePresent())
        );

        resetBtn.disableProperty()
                .bind(pampaSimViewModel.getSimulationRunning());
        loadSpecBtn.disableProperty()
                .bind(pampaSimViewModel.getSimulationRunning());
        saveSpecBtn.disableProperty()
                .bind(pampaSimViewModel.getScenarioIsSaved());
    }
    private void bindTimeLineProperty() {
        pampaSimViewModel.getSimulationRunning().addListener((obs, wasRunning, isRunning) -> {
            stopBtn.setDisable(!isRunning);
            if (!isRunning) {
                animation.pause();
            }
        });
    }
    @FXML
    public void onStepSimulation(ActionEvent actionEvent) {
        pampaSimViewModel.runSimulation(true);
    }

    public void onSelectStatistics(ActionEvent actionEvent) {
        var statisticsViewTuple = FluentViewLoader
                .fxmlView(SimulationStatisticsView.class)
                .viewModel(pampaSimViewModel.getSimulationStatisticsViewModel())
                .load();

        var statisticsView = statisticsViewTuple.getView();

        Stage stage = new Stage();
        stage.setTitle("Estatísticas da Simulação");
        stage.setScene(new Scene(statisticsView));
        stage.setResizable(true);
        stage.minWidthProperty().set(330);
        stage.show();
    }

    private void updateAnimationRate() {
        int v = (int)stepFreqSlider.getValue();
        int v_min = (int)stepFreqSlider.getMin();
        int v_max = (int)stepFreqSlider.getMax();
        if (v == v_min) {
            this.pampaSimViewModel.getSimulationRunning().set(false);
            this.stepFreqReadOut.setText("⏹");
        }
        else if (v == v_max) {
            // todo
            this.stepFreqReadOut.setText("∞");
        }
        else {
            double lowestFreq = 1.0/4.0;
            double highestFreq = 200;
            double scale = (Math.log(highestFreq) - Math.log(lowestFreq))/(v_max - v_min - 2);
            double out = Math.exp(Math.log(lowestFreq) + scale*(v - 1));
            out = ((double)((int)Math.round((out*4))))/4.0; // smallest delta = 1/4
            this.stepFreqReadOut.setText(String.format("%4.2f\nHz", out));
            this.animation.setRate(out);
        }
    }
    private void bindStepFreqSlider() {
        stepFreqSlider.valueProperty().addListener(ev -> {
            updateAnimationRate();
        });
    }

    public void bindGanttChartObservable() {
        ganttChart.setItems(ganttLines);
        pampaSimViewModel.getGanttData().addListener(new MapChangeListener<PidAllocator.Pid, ObservableMap<Integer, Process.State>>() {
            @Override
            public void onChanged(Change<? extends PidAllocator.Pid, ? extends ObservableMap<Integer, Process.State>> change) {
                if (change.wasAdded()) {
                    var val = Bindings.valueAt(pampaSimViewModel.getGanttData(), change.getKey());
                    var observableLine = val.flatMap(
                            stateMap -> Bindings.createObjectBinding(
                                    () -> new GanttLine(change.getKey(), stateMap), val));
                    ganttLines.add(observableLine);
                    LOGGER.debug("added gantt info for PID {} = {}", change.getKey(), observableLine.toString());
                } else if (change.wasRemoved()) {
                    LOGGER.debug("simulation removed gantt info for PID {}", change.getKey());
                    ganttLines.removeIf(line -> line.getValue().pid == change.getKey());
                }
            }
        });

        ganttPidCol.setCellValueFactory(cellData ->
                cellData.getValue().map(GanttLine::pid));

        pampaSimViewModel // what a ride
                .simulatedScenario
                .getSimulation()
                .addListener((observableSim, oldSim, sim) ->
                        sim.getRealClock()
                                .addListener((observableClock, _oldNumber, number) -> {
                                    LOGGER.info("adding column for {}", number);
                                    if (number.intValue() < ganttChart.getColumns().size()) { return; } // don't rollback
                                    var col = new TableColumn<ObservableValue<GanttLine>, Process.State>(number.toString());
                                    col.setCellValueFactory(cellData ->
                                            Bindings.valueAt(cellData.getValue().getValue().stateMap, number.intValue())
                                    );
                                    ganttChart.getColumns().add(col);
                                }));
    }
}
