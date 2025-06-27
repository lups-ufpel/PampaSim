package org.pampasim.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.ViewListBinder;
import org.pampasim.resources.Process;
import org.pampasim.viewModel.PampaSimViewModel;
import org.pampasim.viewModel.ProcessViewModel;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;

public class PampaSimView implements FxmlView<PampaSimViewModel>, Initializable {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimView.class);
    @InjectViewModel
    private PampaSimViewModel pampaSimViewModel;
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
        pampaSimViewModel.resetSimulation();
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

        pampaSimViewModel.setTabPane(moduleTabPane);

        ViewListBinder.bind(
                Map.of(
                        Process.State.NEW, NewList,
                        Process.State.READY, ReadyList,
                        Process.State.RUNNING, RunningList,
                        Process.State.TERMINATED, FinishedList),
                pampaSimViewModel.getAllProcesses(), ViewListBinder.mvvmfxFxmlFactory(ProcessView.class)
        );

        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> pampaSimViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
        bindTimeLineProperty();

        genGraphs.setAllowIndeterminate(false);
        genGraphs.setSelected(false);
        pampaSimViewModel.getGenGraphs().bind(genGraphs.selectedProperty());
        runBtn.disableProperty().bind(
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
        colorCol.setCellValueFactory( cellData -> cellData.getValue().getColorProperty());
        pidCol.setCellValueFactory(cellData -> cellData.getValue().getPid());
        stateCol.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        arrivalCol.setCellValueFactory(cellData -> cellData.getValue().getArrivalTick());
        priorityCol.setCellValueFactory(cellData -> cellData.getValue().getPriority());
        burstCol.setCellValueFactory(cellData -> cellData.getValue().getPriority());
        progressCol.setCellValueFactory(cellData -> cellData.getValue().getProgress());
    }
    private void bindTimeLineProperty() {
        pampaSimViewModel.getSimulationRunning().addListener((obs, wasRunning, isRunning) -> {
            stopBtn.setDisable(!isRunning);
            if (!isRunning) {
                animation.pause();
            }
        });
    }
}
