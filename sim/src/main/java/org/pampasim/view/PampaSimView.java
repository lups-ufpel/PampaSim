package org.pampasim.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.resources.ViewListBinder;
import org.pampasim.resources.Process;
import org.pampasim.resources.view.ProcessView;
import org.pampasim.viewModel.PampaSimViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;

public class PampaSimView implements FxmlView<PampaSimViewModel>, Initializable {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimView.class);
    public Button stepBtn;
    @InjectViewModel
    private PampaSimViewModel pampaSimViewModel;
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
    public Button selectSchedBtn;
    @FXML
    public Button loadSpecBtn;
    @FXML
    public Button saveSpecBtn;
    @FXML
    public CheckBox genGraphs;
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

    private Timeline animation;
    private ProcessViewModel editedProcessViewModel = null;

    private ObservableList<ProcessViewModel> processList = FXCollections.observableArrayList();

    @FXML
    public void onStartSimulation(ActionEvent actionEvent) {
        pampaSimViewModel.startSimulation();
        if(pampaSimViewModel.getSimulationRunning().get()) {
            LOGGER.debug("started animation");
            animation.play();
        }
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
    public void onSelectScheduler(ActionEvent actionEvent) {
        pampaSimViewModel.openSelectSchedulerDialog();
    }
    @FXML
    public void onSelectModule(ActionEvent actionEvent) {
        pampaSimViewModel.openAddModuleDialog();
    }
    @FXML
    public void loadSpec() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open specification file");
        File file = fileChooser.showOpenDialog(null);
        pampaSimViewModel.loadSpec(Paths.get(file.getPath()));
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
                        Process.State.TERMINATED, FinishedList),
                ProcessProgress,
                ProcessViewModel::getProgress,
                pampaSimViewModel.getAllProcesses(),
                ViewListBinder.mvvmfxFxmlFactory(ProcessView.class),
                Process.State.RUNNING
        );

        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> pampaSimViewModel.runSimulation(false)));
        this.animation.setCycleCount(Timeline.INDEFINITE);
        bindTimeLineProperty();

        genGraphs.setAllowIndeterminate(false);
        genGraphs.setSelected(false);
        pampaSimViewModel.getGenGraphs().bind(genGraphs.selectedProperty());

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
        resetBtn.disableProperty()
                .bind(pampaSimViewModel.getSimulationRunning());
        loadSpecBtn.disableProperty()
                .bind(pampaSimViewModel.getSimulationRunning().or(pampaSimViewModel.getScenarioIsSaved().not()));
        saveSpecBtn.disableProperty()
                .bind(pampaSimViewModel.getScenarioIsSaved());
        pampaSimViewModel.updateProps();

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

        Stage stage = new Stage();
        stage.setTitle("Estatísticas da Simulação");
        stage.setScene(new Scene(statisticsViewTuple.getView()));
        stage.setResizable(false);
        stage.show();
    }

}
