package org.pampasim.GUI.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import org.pampasim.GUI.model.CreateProcessDialog;
import org.pampasim.GUI.model.InfoProcessDialog;
import org.pampasim.GUI.model.SchedulerChoiceDialog;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.Os;
import org.pampasim.Os.Process;
import org.pampasim.Os.Scheduler;
import org.pampasim.GUI.controllers.ProcessViewController;

import java.util.Optional;

public class SimulationViewModel {
    public final ObservableList<ProcessViewController> createdList = FXCollections.observableArrayList();
    public final ObservableList<ProcessViewController> runningList = FXCollections.observableArrayList();
    public final ObservableList<ProcessViewController> readyList = FXCollections.observableArrayList();
    public final ObservableList<ProcessViewController> finishedList = FXCollections.observableArrayList();

    public final ObservableList<ProcessViewController> processList = FXCollections.observableArrayList();
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
    public boolean schedulerDialog(){
        Dialog<ButtonType> schedulerChoiceDialog = new SchedulerChoiceDialog();
        Optional<ButtonType> schedulerChoiceDialogResult = schedulerChoiceDialog.showAndWait();
        if (schedulerChoiceDialogResult.isPresent() && schedulerChoiceDialogResult.get() == ButtonType.OK) {
            System.out.println("Scheduler escolhido");
            Scheduler scheduler = (Scheduler) Mediator.getInstance().retrieveComponent(Mediator.Component.SCHEDULER);
            Os kernel = (Os) Mediator.getInstance().retrieveComponent(Mediator.Component.KERNEL);
            kernel.dispatchAll(scheduler);
            System.out.println("parei aqui");
        }
        //FIX LINE BELOW
        return schedulerChoiceDialogResult.isPresent() && schedulerChoiceDialogResult.get() == ButtonType.OK;
    }

    public void AddProcess(Process process) {
        createdList.get(createdList.size() -1).setProcess(process);
    }
    public void createProcess(ProcessViewController processViewController) {
        CreateProcessDialog createProcessDialog = new CreateProcessDialog();
        burst.bind(createProcessDialog.burstProperty());
        priority.bind(createProcessDialog.priorityProperty());
        arrivalTime.bind(createProcessDialog.timeProperty());
        color.bind(createProcessDialog.colorProperty());
        Optional<ButtonType> createProcessDialogResult = createProcessDialog.showAndWait();

        if(createProcessDialogResult.isPresent() && createProcessDialogResult.get() == ButtonType.OK) {
            processViewController.setCircleColor(color.get());
            processList.add(processViewController);
            createdList.add(processViewController);
            Mediator.getInstance().send(this, Mediator.Action.CREATE);
        }
    }
    public void dispatchProcess(Process processToDispatch) {
        ProcessViewController processViewController = findProcessView(processToDispatch,readyList);
        if (processViewController == null){
            System.out.println("Em dispatchProcess is true ou processview e nulo");
        }
        readyList.remove(processViewController);
        runningList.add(processViewController);
    }
    public ProcessViewController findProcessView(Process process, ObservableList<ProcessViewController> list) {
        for (ProcessViewController processViewController : list) {
            if (processViewController.getProcess().equals(process)) {
                return processViewController;
            }
        }
        return null;
    }
    public void interruptProcess(Process processToInterrupt){
        ProcessViewController processViewController = findProcessView(processToInterrupt,runningList);
        runningList.remove(processViewController);
        readyList.add(processViewController);
    }
    public void submitProcess(Process processToSubmit){
        ProcessViewController processViewController = findProcessView(processToSubmit,createdList);
        if (processViewController == null){
            System.out.println("Em dispatchProcess is true ou processview e nulo");
        }
        createdList.remove(processViewController);
        readyList.add(processViewController);
    }
    public void finishProcess(Process processToFinish) {
        ProcessViewController processViewController = findProcessView(processToFinish,runningList);
        runningList.remove(processViewController);
        finishedList.add(processViewController);
    }

    public void showProcessInfo(Process process) {
        InfoProcessDialog infoProcessDialog = new InfoProcessDialog(process);
        infoProcessDialog.showAndWait();
    }
    public IntegerProperty arrivalTimeProperty() {
        return arrivalTime;
    }
    public IntegerProperty burstProperty() {
        return burst;
    }
    public IntegerProperty priorityProperty() {
        return priority;
    }

    public void updateProcessProgress(Process object, double progress) {
        ProcessViewController processViewController = findProcessView(object,runningList);
        if (processViewController != null){
            processViewController.setExecutionProgress(progress);
        }
    }
}