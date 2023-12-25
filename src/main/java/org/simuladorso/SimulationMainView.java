package org.simuladorso;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.simuladorso.GUI.SimulationViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SimulationMainView {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationMainView.class);

    private final SimulationViewModel simulationViewModel;

    @FXML
    public Circle CpuContainer1;
    @FXML
    public HBox NewList, ReadyList;
    public HBox FinishedList;
    Timeline animation;

    public SimulationMainView(){
        this.simulationViewModel = new SimulationViewModel();
        bindViewModel();
    }
    @FXML
    public void onStartSimulation(ActionEvent actionEvent) {
        animation = new Timeline(new KeyFrame(Duration.millis(500), e -> simulationViewModel.runSimulation()));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    @FXML
    public void onFinishSimulation(ActionEvent actionEvent) {
        animation.pause();
        //TODO: add finish simulation logic

    }
    @FXML
    public void onCreateProcess(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/ProcessView.fxml"));
        VBox rootElement = new VBox();
        try {
            // Load the FXML and add it to the root element of ProcessView
            rootElement.getChildren().add(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ProcessView processView = loader.getController();
        processView.setViewModel(this.simulationViewModel);
        processView.setRootElement(rootElement);
        simulationViewModel.createProcess(processView);
    }

    private void bindViewModel() {
        simulationViewModel.createdList.addListener((ListChangeListener.Change<? extends ProcessView> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessView procView : change.getAddedSubList()) {
                        NewList.getChildren().add(procView.getProcessCircle());
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessView procView: change.getRemoved()) {
                        NewList.getChildren().remove(procView.getProcessCircle());
                    }
                }
            }
        });
        simulationViewModel.readyList.addListener((ListChangeListener.Change<? extends ProcessView> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessView procView : change.getAddedSubList()) {
                        this.ReadyList.getChildren().add(procView.getProcessCircle());
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessView procView: change.getRemoved()) {
                        this.ReadyList.getChildren().remove(procView.getProcessCircle());
                    }
                }
            }
        });
        simulationViewModel.runningList.addListener((ListChangeListener.Change<? extends ProcessView> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessView procView : change.getAddedSubList()) {
                        this.CpuContainer1.setFill(procView.getProcessCircle().getFill());
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessView procView: change.getRemoved()) {
                        this.CpuContainer1.setFill(Paint.valueOf("white"));
                    }
                }
            }
        });
        simulationViewModel.finishedList.addListener((ListChangeListener.Change<? extends ProcessView> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (ProcessView procView : change.getAddedSubList()) {
                        this.FinishedList.getChildren().add(procView.getProcessCircle());
                    }
                } else if (change.wasRemoved()) {
                    for (ProcessView procView: change.getRemoved()) {
                        this.FinishedList.getChildren().remove(procView.getProcessCircle());
                    }
                }
            }
        });
    }
}
