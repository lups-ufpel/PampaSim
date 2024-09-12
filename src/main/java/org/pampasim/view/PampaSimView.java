package org.pampasim.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.pampasim.SimResources.Process;
import org.pampasim.viewModel.PampaSimViewModel;
import org.pampasim.viewModel.ProcessViewModel;
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
        pampaSimViewModel.startSimulation();
        if(pampaSimViewModel.isSimulationRunning()) {
            animation.play();
        }
    }
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
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createProcessDialog = new Dialog<>();
        selectSchedulerDialog = new Dialog<>();
        var processDialogPane = loadDialogPane(CreateProcessDialogView.class, pampaSimViewModel.getProcessScope());
        var schedulerDialogPane = loadDialogPane(SelectSchedulerDialogView.class, pampaSimViewModel.getSchedulerScope());
        configureDialog(createProcessDialog,"Create Process Window",processDialogPane,this::handleCreateProcessResult);
        configureDialog(selectSchedulerDialog,"Select Scheduler",schedulerDialogPane,this::handleSelectSchedulerResult);
        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> pampaSimViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
        bindTimeLineProperty();

        pampaSimViewModel.getProcesses().addListener((ListChangeListener<ProcessViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewModel process : change.getAddedSubList()) {
                        addProcessToUI(process);
                    }
                }
            }
        });
    }
    private Circle createCircleForProcess(ProcessViewModel process) {
        Circle circle = new Circle(30, process.getColor());
        circle.setId(process.pid());
        circle.setUserData(process.getPriority());
        return circle;
    }
    private void addProcessToUI(ProcessViewModel process) {
        Circle circle = createCircleForProcess(process);

        // Adiciona o processo ao container correto com base no estado
        switch (process.getState()) {
            case NEW:
                NewList.getChildren().add(circle);
                break;
            case READY:
                ReadyList.getChildren().add(circle);
                break;
            case TERMINATED:
                FinishedList.getChildren().add(circle);
                break;
        }

        // Observa mudanças de estado do processo para mover automaticamente o círculo entre os containers
        process.stateProperty().addListener((obs, oldState, newState) -> {
            moveCircleToCorrectContainer(circle, newState);
        });
    }
    private void moveCircleToCorrectContainer(Circle circle, Process.State newState) {
        // Remove o círculo de todos os containers
        NewList.getChildren().remove(circle);
        ReadyList.getChildren().remove(circle);
        FinishedList.getChildren().remove(circle);
        CpuContainer1.setFill(Color.TRANSPARENT);
        // Adiciona ao container correto com base no novo estado
        switch (newState) {
            case NEW:
                NewList.getChildren().add(circle);
                break;
            case READY:
                ReadyList.getChildren().add(circle);
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
        pampaSimViewModel.getSimulationRunningProperty().addListener((obs, wasRunning, isRunning) -> {
            runBtn.setDisable(isRunning);
            stopBtn.setDisable(!isRunning);
            if (!isRunning) {
                animation.pause();
            }
        });
    }
}
