package org.pampasim.gui.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
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
import javafx.util.Duration;
import org.pampasim.gui.viewmodel.SimulationViewModel;
import org.pampasim.gui.viewmodel.CreateProcessDialogViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class PampaSimView implements FxmlView<SimulationViewModel>, Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimView.class);
    @InjectViewModel
    private SimulationViewModel simulationViewModel;
    @FXML
    public Circle CpuContainer1;
    public Circle CpuContainertemp;
    @FXML
    public HBox NewList;
    public HBox ReadyList;
    public HBox FinishedList;
    public Button runBtn;
    public Button stopBtn;
    private Timeline animation;
    @FXML
    public void onStartSimulation(ActionEvent actionEvent) {
        boolean canStart = simulationViewModel.schedulerDialog();
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
    @FXML
    public void createProcess(ActionEvent actionEvent) {
        final ViewTuple<CreateProcessDialogView,CreateProcessDialogViewModel> viewTuple = FluentViewLoader.fxmlView(CreateProcessDialogView.class)
                .providedScopes(simulationViewModel.getProcessScope())
                .load();
        final DialogPane dialogPane = (DialogPane) viewTuple.getView();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Process window");
        dialog.setDialogPane(dialogPane);
        dialog.setResultConverter(this::handleResult);
        dialog.showAndWait();
    }
    private ButtonType handleResult(ButtonType buttonType) {
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