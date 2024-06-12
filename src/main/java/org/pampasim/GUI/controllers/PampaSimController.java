package org.pampasim.GUI.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.pampasim.GUI.model.SimulationViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PampaSimController implements Initializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PampaSimController.class);
    private final SimulationViewModel simulationViewModel;
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

    public PampaSimController() {
        this.simulationViewModel = new SimulationViewModel();
        this.animation = new Timeline(new KeyFrame(Duration.millis(500), e -> simulationViewModel.runSimulation()));
        this.animation.setCycleCount(Timeline.INDEFINITE);
        bindViewModel();
    }
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
    public void onCreateProcess(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ProcessView.fxml"));
        VBox rootElement = null;
        try {
            // Load the FXML and add it to the root element of ProcessView
            rootElement = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessViewController processViewController = loader.getController();
        processViewController.setScene(this.scene);
        processViewController.setViewModel(this.simulationViewModel);
        processViewController.setRootElement(rootElement);
        simulationViewModel.createProcess(processViewController);
        System.out.println(simulationViewModel.processList.size());
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
