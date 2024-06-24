package org.pampasim.gui.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.pampasim.gui.model.SimulationViewModel;
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
    @FXML
    public HBox NewList, ReadyList;
    public HBox FinishedList;
    public ProgressBar ProcessProgress;
    public Button runBtn;
    public Button stopBtn;
    @FXML
    public TableView<ProcessViewController> procTable;
    @FXML
    public TableColumn<ProcessViewController,Integer> pidCol;
    @FXML
    public TableColumn<ProcessViewController, Circle> colorCol;
    @FXML
    public TableColumn<ProcessViewController,ProgressBar> progressCol;
    @FXML
    private TableColumn<ProcessViewController, Integer> burstCol;
    @FXML
    private TableColumn<ProcessViewController, Integer> priorityCol;
    @FXML
    private TableColumn<ProcessViewController, Integer> arrivalCol;
    @FXML
    private TableColumn<ProcessViewController,String> stateCol;
    private Timeline animation;

    private Scene scene;

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
        dialog.setResultConverter(buttonType -> {
            if (buttonType.getButtonData() == ButtonBar.ButtonData.APPLY) {
                System.out.println("clicked ok!");
                System.out.println("burst" + simulationViewModel.getProcessScope().getBurstProperty().getValue());
                System.out.println("priority" + simulationViewModel.getProcessScope().getPriorityProperty().getValue());
                System.out.println("burst from dialog " + viewTuple.getViewModel().getBurstProperty().getValue());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void bindViewModel() {
        simulationViewModel.createdList.addListener((ListChangeListener.Change<? extends ProcessViewController> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewController procView : change.getAddedSubList()) {
                        NewList.getChildren().add(procView.getProcessCircle());
                        if (runBtn.isDisable()){
                            runBtn.setDisable(false);
                        }
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessViewController procView: change.getRemoved()) {
                        NewList.getChildren().remove(procView.getProcessCircle());
                    }
                }
            }
        });
        simulationViewModel.readyList.addListener((ListChangeListener.Change<? extends ProcessViewController> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewController procView : change.getAddedSubList()) {
                        this.ReadyList.getChildren().add(procView.getProcessCircle());
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessViewController procView: change.getRemoved()) {
                        this.ReadyList.getChildren().remove(procView.getProcessCircle());
                    }
                }
            }
        });
        simulationViewModel.runningList.addListener((ListChangeListener.Change<? extends ProcessViewController> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewController procView : change.getAddedSubList()) {
                        this.CpuContainer1.setFill(procView.getProcessCircle().getFill());
                        this.CpuContainer1.setOnMouseClicked(procView::showProcessInfo);
                        this.ProcessProgress.progressProperty().bind(procView.getProcess().execTimeSliceProperty().divide(procView.getProcess().burstProperty()));
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessViewController procView: change.getRemoved()) {
                        this.CpuContainer1.setFill(Paint.valueOf("white"));
                        this.CpuContainer1.setOnMouseClicked(null);
                        this.ProcessProgress.progressProperty().unbind();
                        this.ProcessProgress.setProgress(0);
                    }
                }
            }
        });
        simulationViewModel.finishedList.addListener((ListChangeListener.Change<? extends ProcessViewController> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessViewController procView : change.getAddedSubList()) {
                        this.FinishedList.getChildren().add(procView.getProcessCircle());
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessViewController procView: change.getRemoved()) {
                        this.FinishedList.getChildren().remove(procView.getProcessCircle());
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindViewModel();
        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> simulationViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
        pidCol.setCellValueFactory(cellData -> cellData.getValue().process.pidProperty().asObject());
        burstCol.setCellValueFactory(cellData -> cellData.getValue().process.burstProperty().asObject());
        priorityCol.setCellValueFactory(cellData -> cellData.getValue().process.priorityProperty().asObject());
        arrivalCol.setCellValueFactory(cellData -> cellData.getValue().process.arrivalProperty().asObject());
        stateCol.setCellValueFactory(cellData -> cellData.getValue().process.stateProperty());
        colorCol.setCellValueFactory(cellData -> cellData.getValue().circleForTableView());
        progressCol.setCellValueFactory(cellData -> cellData.getValue().progressForTableView());
        procTable.setItems(simulationViewModel.processList);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
