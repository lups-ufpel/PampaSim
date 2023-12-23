package org.simuladorso.GUI;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import org.simuladorso.GUI.model.CreateProcessDialog;
import org.simuladorso.GUI.model.InfoProcessDialog;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Process;
import java.util.Optional;

public class SimulationViewModel {
    public final ObservableList<Process> createdList = FXCollections.observableArrayList();
    public final ObservableList<Process> runningList = FXCollections.observableArrayList();
    public final ObservableList<Process> readyList = FXCollections.observableArrayList();
    public final ObservableList<Process> finishedList = FXCollections.observableArrayList();
    private final IntegerProperty arrivalTime = new SimpleIntegerProperty();
    private final IntegerProperty burst = new SimpleIntegerProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final IntegerProperty priority = new SimpleIntegerProperty();

    public SimulationViewModel() {
        Mediator.getInstance().registerComponent(this, Mediator.Component.GUI);
    }
    public void runSimulation() {
        Mediator.getInstance().send(this, Mediator.Action.RUN);
    }
    public void AddProcess(Process process) {
        createdList.add(process);
    }
    public void createProcess() {
        CreateProcessDialog createProcessDialog = new CreateProcessDialog();
        burst.bind(createProcessDialog.burstProperty());
        priority.bind(createProcessDialog.priorityProperty());
        arrivalTime.bind(createProcessDialog.timeProperty());
        color.bind(createProcessDialog.colorProperty());
        Optional<ButtonType> createProcessDialogResult = createProcessDialog.showAndWait();
        if(createProcessDialogResult.isPresent() && createProcessDialogResult.get() == ButtonType.OK){
            Mediator.getInstance().send(this, Mediator.Action.CREATE);
        }
    }
    public void showProcessInfo(Process p) {
        System.out.println("showProcessInfo");
        Dialog<ButtonType> processInfoDialog = new InfoProcessDialog(p);
        Optional<ButtonType> processInfoDialogResult = processInfoDialog.showAndWait();
    }

    public  void dispatchProcess(Process processToDispatch){
        Boolean t = readyList.remove(processToDispatch);
        System.out.println(t);
        runningList.add(processToDispatch);
    }
    public void interruptProcess(Process processToInterrupt){
        runningList.remove(processToInterrupt);
        readyList.add(processToInterrupt);
    }
    public void submitProcess(Process processToSubmit){
        createdList.remove(processToSubmit);
        readyList.add(processToSubmit);
    }
    public void finishProcess(Process processToFinish){
        runningList.remove(processToFinish);
        finishedList.add(processToFinish);
    }
    public IntegerProperty arrivalTimeProperty() {
        return arrivalTime;
    }
    public IntegerProperty burstProperty() {
        return burst;
    }
    public ObjectProperty<Color> colorProperty() {
        return color;
    }
    public IntegerProperty priorityProperty() {
        return priority;
    }
}