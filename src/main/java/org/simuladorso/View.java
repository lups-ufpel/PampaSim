package org.simuladorso;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.simuladorso.GUI.SimulationViewModel;
import org.simuladorso.Os.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class View extends VBox {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

    private final Map<Process, Circle> processCircleMap = new HashMap<>();

    private final SimulationViewModel simulationViewModel;
    public VBox root = new VBox();

    @FXML
    public VBox CpuContainer1;
    @FXML
    public VBox CpuContainer2;
    @FXML
    public HBox NewList;
    @FXML
    public HBox ReadyList;
    Timeline animation;

    public View() throws IOException{
        this.simulationViewModel = new SimulationViewModel();
        loadFXML();
        bindViewModel();
    }
    private void loadFXML() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainWindowGUI.fxml"));
        loader.setController(this);
        if (loader.getLocation() == null)  {
            throw new IOException("FXML file not found");
        }

        try {
            this.root.getChildren().add(loader.load());
        } catch (IOException e) {
            LOGGER.error("Error loading FXML", e);
        }
    }
    public Circle createCircle() {
        Color color = simulationViewModel.colorProperty().get();
        return new Circle(35,color);
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
        simulationViewModel.createProcess();
    }

    private void bindViewModel() {
        simulationViewModel.createdList.addListener((ListChangeListener.Change<? extends Process> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Process process : change.getAddedSubList()) {
                        Circle newCircle = createCircle();
                        newCircle.setOnMouseClicked((evt) -> simulationViewModel.showProcessInfo(process));
                        NewList.getChildren().add(newCircle);
                        processCircleMap.put(process, newCircle);
                    }
                } else if (change.wasRemoved()) {
                    for (Process process : change.getRemoved()) {
                        Circle circleToRemove = processCircleMap.get(process);
                        NewList.getChildren().remove(circleToRemove);
                    }
                }
            }
        });
        simulationViewModel.readyList.addListener((ListChangeListener.Change<? extends Process> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Process process : change.getAddedSubList()) {
                        Circle circleToAdd = processCircleMap.get(process);
                        ReadyList.getChildren().add(circleToAdd);
                    }
                } else if (change.wasRemoved()) {
                    for (Process process : change.getRemoved()) {
                        Circle circleToRemove = processCircleMap.get(process);
                        ReadyList.getChildren().remove(circleToRemove);
                    }
                }
            }
        });
        simulationViewModel.runningList.addListener((ListChangeListener.Change<? extends Process> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Process process : change.getAddedSubList()) {
                        Circle circleToAdd = processCircleMap.get(process);
                        CpuContainer1.getChildren().add(circleToAdd);
                    }
                } else if (change.wasRemoved()) {
                    for (Process process : change.getRemoved()) {
                        Circle circleToRemove = processCircleMap.get(process);
                        CpuContainer1.getChildren().remove(circleToRemove);
                    }
                }
            }
        });
        simulationViewModel.finishedList.addListener((ListChangeListener.Change<? extends Process> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Process process : change.getAddedSubList()) {
                        Circle circleToAdd = processCircleMap.get(process);
                        CpuContainer2.getChildren().add(circleToAdd);
                    }
                } else if (change.wasRemoved()) {
                    for (Process process : change.getRemoved()) {
                        Circle circleToRemove = processCircleMap.get(process);
                        CpuContainer2.getChildren().remove(circleToRemove);
                    }
                }
            }
        });
    }
}
