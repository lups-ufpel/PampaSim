package org.pampasim.gui.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.pampasim.gui.ViewModelEvents.ProcessCreateEvent;
import org.pampasim.gui.ViewModelEvents.ProcessFinishEvent;
import org.pampasim.gui.ViewModelEvents.ProcessReadyEvent;
import org.pampasim.gui.ViewModelEvents.ProcessStartRunningEvent;
import org.pampasim.gui.viewmodel.PampaSimViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class PampaSimView implements FxmlView<PampaSimViewModel>, Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimView.class);
    @InjectViewModel
    private PampaSimViewModel pampaSimViewModel;
    @FXML
    public Circle CpuContainer1;
    public Circle CpuContainerTemp;
    @FXML
    public HBox NewList;
    @FXML
    public HBox ReadyList;
    @FXML
    public HBox FinishedList;
    @FXML
    public Button runBtn;
    @FXML
    public Button stopBtn;
    @FXML
    public Button selectSchedBtn;
    private Timeline animation;
    private Dialog<ButtonType> createProcessDialog;
    private Dialog<ButtonType> selectSchedulerDialog;

    @FXML
    public void onStartSimulation(ActionEvent actionEvent) {
        boolean canStart = pampaSimViewModel.startSimulation();
        if (!canStart) {
            return;
        }
        animation.play();
        stopBtn.setDisable(false);
    }
    @FXML
    public void onFinishSimulation(ActionEvent actionEvent) {
        animation.pause();
        stopBtn.setDisable(true);
    }
    private void configureDialog(Dialog<ButtonType> dialog,String title, DialogPane dialogPane, Callback<ButtonType,ButtonType> resultHandler) {
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
        createProcessDialog.showAndWait();
    }

    @FXML
    public void selectScheduler(ActionEvent actionEvent) {
        selectSchedulerDialog.showAndWait();
    }

    private ButtonType handleSelectSchedulerResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
            pampaSimViewModel.setSimulationScheduler();
        }

        return null;
    }
    private ButtonType handleCreateProcessResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
            pampaSimViewModel.createNewProcess();

            if (pampaSimViewModel.isSchedulerSet())
                runBtn.setDisable(false);
        }

        return null;
    }
    private Circle removeCircleById(ObservableList<Node> list, String id) {
        for (Node node : list) {
            if (node instanceof Circle && id.equals(node.getId())) {
                list.remove(node);
                return (Circle) node;
            }
        }
        return null;
    }
    private void addCircleSortedByPriority(ObservableList<Node> list, Circle circle) {
        int priority = (int) circle.getUserData();
        int index = 0;
        for (Node node : list) {
            if (node instanceof Circle) {
                int nodePriority = (int) ((Circle) node).getUserData();
                if (priority < nodePriority) {
                    break;
                }
            }
            index++;
        }
        list.add(index, circle);
    }
    private void bindViewModel() {
        pampaSimViewModel.subscribe(PampaSimViewModel.NEW_PROCESS, this::handleNewProcess);
        pampaSimViewModel.subscribe(PampaSimViewModel.READY_PROCESS, this::handleReadyProcess);
        pampaSimViewModel.subscribe(PampaSimViewModel.START_RUNNING_PROCESS, this::handleStartRunningProcess);
        pampaSimViewModel.subscribe(PampaSimViewModel.FINISH_PROCESS, this::handleFinishedProcess);
        pampaSimViewModel.subscribe(PampaSimViewModel.STOP_SIMULATION, (key, payload) -> this.animation.stop());
    }
    private void handleNewProcess(String key, Object... payload) {
        if (payload.length > 0 && payload[0] instanceof ProcessCreateEvent processCreateEvent) {
            String colorString = pampaSimViewModel.getProcessScope().getColorProperty().getValue();
            Color selectedColor = Color.web(colorString);
            Circle circle = new Circle(30, selectedColor);
            String id = processCreateEvent.getPid();
            int priority = processCreateEvent.getPriority();
            circle.setId(id);
            circle.setUserData(priority);
            addCircleSortedByPriority(NewList.getChildren(), circle);
        } else {
            throw new IllegalArgumentException("Payload is not of type ProcessCreateEvent");
        }
    }
    private void handleReadyProcess(String key, Object... payload) {
        if(payload.length > 0 && payload[0] instanceof ProcessReadyEvent processReadyEvent) {
            String id = processReadyEvent.getPid();
            Circle circle = removeCircleById(NewList.getChildren(), id);
            if(circle != null) {
                addCircleSortedByPriority(ReadyList.getChildren(), circle);
            }
        } else {
            throw new IllegalArgumentException("Payload is not of type ProcessReadyEvent");
        }
    }
    private void handleStartRunningProcess(String key, Object... payload) {
        if(payload.length > 0 && payload[0] instanceof ProcessStartRunningEvent processStartRunningEvent) {
            String id = processStartRunningEvent.getPid();
            CpuContainerTemp = removeCircleById(ReadyList.getChildren(), id);
            Paint paint = CpuContainer1.getFill();
            CpuContainer1.setFill(CpuContainerTemp.getFill());
            CpuContainerTemp.setFill(paint);
        } else {
            throw new IllegalArgumentException("Payload is not of type ProcessStartRunningEvent");
        }
    }
    private void handleFinishedProcess(String key, Object... payload) {
        if(payload.length > 0 && payload[0] instanceof ProcessFinishEvent processFinishEvent) {
            Paint p = CpuContainer1.getFill();
            CpuContainer1.setFill(CpuContainerTemp.getFill());
            CpuContainerTemp.setFill(p);
            FinishedList.getChildren().add(CpuContainerTemp);
        } else {
            throw new IllegalArgumentException("Payload is not of type ProcessFinishEvent");
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindViewModel();
        createProcessDialog = new Dialog<>();
        selectSchedulerDialog = new Dialog<>();
        var processDialogPane = loadDialogPane(CreateProcessDialogView.class, pampaSimViewModel.getProcessScope());
        var schedulerDialogPane = loadDialogPane(SelectSchedulerDialogView.class, pampaSimViewModel.getSchedulerScope());
        configureDialog(createProcessDialog,"Create Process Window",processDialogPane,this::handleCreateProcessResult);
        configureDialog(selectSchedulerDialog,"Select Scheduler",schedulerDialogPane,this::handleSelectSchedulerResult);
        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> pampaSimViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
    }
}