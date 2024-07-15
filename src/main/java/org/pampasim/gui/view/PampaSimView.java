package org.pampasim.gui.view;

import de.saxsys.mvvmfx.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import javafx.util.Duration;
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
    public Circle CpuContainertemp;
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
    private void bindViewModel() {
        pampaSimViewModel.subscribe(PampaSimViewModel.NEW_PROCESS,(key, payload) -> {
            String colorString = pampaSimViewModel.getProcessScope().getColorProperty().getValue();
            Color selectedColor = Color.web(colorString);
            var circle = new Circle(30, selectedColor);
            NewList.getChildren().add(circle);
        });
        pampaSimViewModel.subscribe(PampaSimViewModel.READY_PROCESS,(key, payload) -> {
            var circle = NewList.getChildren().removeFirst();
            ReadyList.getChildren().add(circle);
        });
        pampaSimViewModel.subscribe(PampaSimViewModel.START_RUNNING_PROCESS,(key, payload) -> {
            CpuContainertemp= (Circle) ReadyList.getChildren().removeFirst();
            var paint = CpuContainer1.getFill();
            CpuContainer1.setFill(CpuContainertemp.getFill());
            CpuContainertemp.setFill(paint);
        });
        pampaSimViewModel.subscribe(PampaSimViewModel.FINISH_PROCESS, (key, payload) -> {
            Paint p = CpuContainer1.getFill();
            CpuContainer1.setFill(CpuContainertemp.getFill());
            CpuContainertemp.setFill(p);
            FinishedList.getChildren().add(CpuContainertemp);
        });
        pampaSimViewModel.subscribe(PampaSimViewModel.STOP_SIMULATION, (key, payload) -> this.animation.stop());
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