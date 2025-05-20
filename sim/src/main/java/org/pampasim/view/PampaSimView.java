package org.pampasim.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.resources.Process;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.scopes.ProcessScope;
import org.pampasim.viewModel.PampaSimViewModel;
import org.pampasim.viewModel.ProcessViewModel;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class PampaSimView implements FxmlView<PampaSimViewModel>, Initializable {
    private static final Logger LOGGER = LogManager.getLogger(PampaSimView.class);
    @InjectViewModel
    private PampaSimViewModel pampaSimViewModel;
    @FXML
    public Circle CpuContainer1;
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

    private Timeline animation;
    private Dialog<ButtonType> createProcessDialog;
    private Dialog<ButtonType> editProcessDialog;
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
    private void configureDialog(Dialog<ButtonType> dialog, String title, DialogPane dialogPane, Callback<ButtonType,ButtonType> resultHandler) {
        dialog.setDialogPane(dialogPane);
        dialog.setTitle(title);
        dialog.setResultConverter(resultHandler);
    }
    private DialogPane loadDialogPane(Class<? extends FxmlView<?>> viewClass, Scope scope) {
        final ViewTuple<?, ?> viewTuple = FluentViewLoader.fxmlView(viewClass)
                .providedScopes(scope)
                .load();
        return (DialogPane) viewTuple.getView();
    }
    @FXML
    public void createProcess(ActionEvent actionEvent) {
        pampaSimViewModel.openCreateProcessDialog();
        //createProcessDialog.showAndWait();
    }
    @FXML
    public void editProcess(MouseEvent mouseEvent) {
        Node processCircle = (Node)mouseEvent.getSource();
        Long creationId = (Long)processCircle.getUserData();
        editedProcessViewModel = pampaSimViewModel.getProcessesByCreationId().get(creationId);
        ProcessScope procScope = pampaSimViewModel.getCreateProcessScope();
        // janky
        //procScope.getPriorityProperty().setValue(editedProcessViewModel.getPriority().getValue());
        //procScope.getDurationProperty().setValue(editedProcessViewModel.getCreationData().getDurationTicks());
        //procScope.getStartTimeProperty().setValue(editedProcessViewModel.getCreationData().getArrivalTick());
        //procScope.getColorProperty().setValue(editedProcessViewModel.getColor().getValue().toString());
        editProcessDialog.showAndWait();
    }
    @FXML
    public void onSelectScheduler(ActionEvent actionEvent) {
        pampaSimViewModel.openSelectSchedulerDialog();
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
    private ButtonType handleCreateProcessResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
            //pampaSimViewModel.createNewProcess();
        }
        return null;
    }

    private ButtonType handleEditProcessResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
            pampaSimViewModel.editProcess(editedProcessViewModel, false);
        } else if (buttonType.getButtonData() == ButtonBar.ButtonData.LEFT) {
            pampaSimViewModel.editProcess(editedProcessViewModel, true);
        }
        return null;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        createProcessDialog = new Dialog<>();
        editProcessDialog = new Dialog<>();
        var createProcessDialogPane = loadDialogPane(CreateProcessDialogView.class, pampaSimViewModel.getCreateProcessScope());
        var editProcessDialogPane = loadDialogPane(EditProcessDialogView.class, pampaSimViewModel.getEditProcessScope());
        LOGGER.debug("scope a {}\nscope b {}", pampaSimViewModel.getCreateProcessScope(), pampaSimViewModel.getEditProcessScope());
        configureDialog(createProcessDialog,"Create Process Window",createProcessDialogPane,this::handleCreateProcessResult);
        configureDialog(editProcessDialog,"Edit Process Window",editProcessDialogPane,this::handleEditProcessResult);
        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> pampaSimViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
        bindTimeLineProperty();


        pampaSimViewModel.getProcessesByCreationId()
                .addListener(
                        (MapChangeListener<Long, ProcessViewModel>) change ->
                        {
            if (change.wasRemoved()) {
                var val = change.getValueRemoved();
                processList.remove(val);
                removeProcessFromUI(change.getValueRemoved());
            }
            if (change.wasAdded()) {
                var val = change.getValueAdded();
                processList.add(val);
                addProcessToUI(val);
            }

            LOGGER.debug("got change {}", change);
        });

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

        colorCol.setCellValueFactory(p -> p.getValue().getColor());
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
        pidCol.setCellValueFactory(p ->
            p.getValue().getPid().map(Object::toString).orElse("Not yet decided")
        );
        stateCol.setCellValueFactory(p -> p.getValue().getState());
        arrivalCol.setCellValueFactory(
                p -> new ReadOnlyObjectWrapper<>(p.getValue().getCreationData().getArrivalTick())
        );
        priorityCol.setCellValueFactory(p -> p.getValue().getPriority().map(Number::intValue));
        burstCol.setCellValueFactory(p -> p.getValue().getBurstTime().map(Number::intValue));

        progressCol.setCellValueFactory(p -> p.getValue().getCurrExecTime().map(n -> // calculates the data for the cell
                n.doubleValue() / p.getValue().getCreationData().getDurationTicks())); // in this case, calculates the % of completion for the process (in the range of 0 to 1)

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
                    int total = process.getCreationData().getDurationTicks();

                    progressLabel.setText(current + "/" + total);
                    setGraphic(stackPane);
                }
            }
        });

        procTable.setItems(processList);
    }
    private Circle createCircleForProcess(ProcessViewModel process) {
        Circle circle = new Circle(30, process.getColor().getValue());
        circle.setId("proc" + String.valueOf(process.getCreationData().getCreationId())); // very important
        circle.setUserData(process.getCreationData().getCreationId());
        circle.setOnMouseClicked(this::editProcess);
        process.setCircleRepr(circle);
        return circle;
    }
    private void addProcessToUI(ProcessViewModel process) {
        createCircleForProcess(process);
        moveProcessToCorrectContainer(process);
        // Observa mudanças de estado do processo para mover automaticamente o círculo entre os containers
        process.getState().addListener((obs, oldState, newState) -> {
            if (oldState == Process.State.RUNNING) {
                CpuContainer1.setFill(Color.TRANSPARENT);
            }
            moveProcessToCorrectContainer(process);
        });
    }
    private void removeProcessFromUI(ProcessViewModel pvm) {
        Circle circle = pvm.getCircleRepr();
        // Remove o círculo de todos os containers
        var nlr = NewList.getChildren().remove(circle);
        var rlr = ReadyList.getChildren().remove(circle);
        var wlr = WaitingList.getChildren().remove(circle);
        var flr = FinishedList.getChildren().remove(circle);
        LOGGER.debug("{} removed from new {} ready {} waiting {} finished {}", circle, nlr, rlr, wlr, flr);
    }
    private void moveProcessToCorrectContainer(ProcessViewModel pvm) {
        Circle circle = pvm.getCircleRepr();
        var state = pvm.getState().getValue();
        removeProcessFromUI(pvm);
        // Adiciona ao container correto com base no novo estado
        switch (state) {
            case NEW:
                NewList.getChildren().add(circle);
                break;
            case READY:
                ReadyList.getChildren().add(circle);
                break;
            case WAITING:
                WaitingList.getChildren().add(circle);
                break;
            case TERMINATED:
                FinishedList.getChildren().add(circle);
                break;
            case RUNNING:
                setProcessToCpuContainer(circle);
                break;
        }
    }
    private void setProcessToCpuContainer(Circle circle) {
        // Exibe o processo em execução na CPU
        CpuContainer1.setFill(circle.getFill());
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
