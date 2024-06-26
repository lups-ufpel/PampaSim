package org.pampasim.gui.view;

import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.internal.viewloader.View;
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
import org.pampasim.gui.viewmodel.SelectSchedulerDialogViewModel;
import org.pampasim.gui.viewmodel.SimulationViewModel;
import org.pampasim.gui.viewmodel.CreateProcessDialogViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;

public class PampaSimView implements FxmlView<SimulationViewModel>, Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimView.class);
    @InjectViewModel
    private SimulationViewModel simulationViewModel;
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

    @FXML
    public void onStartSimulation(ActionEvent actionEvent) {
        boolean canStart = simulationViewModel.startSimulation();
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
    private void showDialog(Class<? extends FxmlView<?>> viewClass, String title, Scope scope, Callback<ButtonType,ButtonType> resultHandler) {
        final ViewTuple<?, ?> viewTuple = FluentViewLoader.fxmlView(viewClass)
                .providedScopes(scope)
                .load();
        final DialogPane dialogPane = (DialogPane) viewTuple.getView();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setDialogPane(dialogPane);
        dialog.setResultConverter(resultHandler);
        dialog.showAndWait();
    }
    @FXML
    public void createProcess(ActionEvent actionEvent) {
        showDialog(CreateProcessDialogView.class,"Create Process Window",simulationViewModel.getProcessScope(),this::handleCreateProcessResult);
    }
    @FXML
    public void selectScheduler(ActionEvent actionEvent) {
        showDialog(SelectSchedulerDialogView.class,"Select Scheduler Window",simulationViewModel.getSchedulerScope(),this::handleSelectSchedulerResult);
    }
    private ButtonType handleSelectSchedulerResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
            simulationViewModel.setSimulationScheduler();
        }
        return null;
    }
    private ButtonType handleCreateProcessResult(ButtonType buttonType) {
        if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
            simulationViewModel.createNewProcess();
            runBtn.setDisable(false);
        }
        return null;
    }
    private void bindViewModel() {
        simulationViewModel.subscribe(SimulationViewModel.NEW_PROCESS,(key, payload) -> {
            String colorString = simulationViewModel.getProcessScope().getColorProperty().getValue();
            System.out.println("color string"+ colorString);
            Color selectedColor = Color.web(colorString);
            var circle = new Circle(30, selectedColor);
            NewList.getChildren().add(circle);
        });
        simulationViewModel.subscribe(SimulationViewModel.START_PROCESS,(key, payload) -> {
            var circle = NewList.getChildren().remove(0);
            ReadyList.getChildren().add(circle);
        });
        simulationViewModel.subscribe(SimulationViewModel.RUN_PROCESS,(key, payload) -> {
            CpuContainertemp= (Circle) ReadyList.getChildren().remove(0);
            var paint = CpuContainer1.getFill();
            CpuContainer1.setFill(CpuContainertemp.getFill());
            CpuContainertemp.setFill(paint);
        });
        simulationViewModel.subscribe(SimulationViewModel.FINISH_PROCESS, (key, payload) -> {
            Paint p = CpuContainer1.getFill();
            CpuContainer1.setFill(CpuContainertemp.getFill());
            CpuContainertemp.setFill(p);
            FinishedList.getChildren().add(CpuContainertemp);
        });
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindViewModel();
        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> simulationViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
    }
}