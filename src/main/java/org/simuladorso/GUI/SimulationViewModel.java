package org.simuladorso.GUI;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.paint.Color;
import org.simuladorso.GUI.model.CreateProcessDialog;
import org.simuladorso.GUI.model.InfoProcessDialog;
import org.simuladorso.GUI.model.SchedulerChoiceDialog;
import org.simuladorso.Mediator.Mediator;
import org.simuladorso.Os.Os;
import org.simuladorso.Os.Process;
import org.simuladorso.Os.Scheduler;
import org.simuladorso.ProcessView;

import java.util.Optional;

public class SimulationViewModel {
    public final ObservableList<ProcessView> createdList = FXCollections.observableArrayList();
    public final ObservableList<ProcessView> runningList = FXCollections.observableArrayList();
    public final ObservableList<ProcessView> readyList = FXCollections.observableArrayList();
    public final ObservableList<ProcessView> finishedList = FXCollections.observableArrayList();

    public final ObservableList<ProcessView> processList = FXCollections.observableArrayList();
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
    public void createProcess(ProcessView processView) {
        CreateProcessDialog createProcessDialog = new CreateProcessDialog();
        burst.bind(createProcessDialog.burstProperty());
        priority.bind(createProcessDialog.priorityProperty());
        arrivalTime.bind(createProcessDialog.timeProperty());
        color.bind(createProcessDialog.colorProperty());
        Optional<ButtonType> createProcessDialogResult = createProcessDialog.showAndWait();

        if(createProcessDialogResult.isPresent() && createProcessDialogResult.get() == ButtonType.OK) {
            processView.setCircleColor(color.get());
            processList.add(processView);
            createdList.add(processView);
            Mediator.getInstance().send(this, Mediator.Action.CREATE);
        }
    }
    public void dispatchProcess(Process processToDispatch) {
        ProcessView processView = findProcessView(processToDispatch,readyList);
        if (processView == null){
            System.out.println("Em dispatchProcess is true ou processview e nulo");
        }
        readyList.remove(processView);
        runningList.add(processView);
    }
    public ProcessView findProcessView(Process process, ObservableList<ProcessView> list) {
        for (ProcessView processView : list) {
            if (processView.getProcess().equals(process)) {
                return processView;
            }
        }
        return null;
    }
    public void interruptProcess(Process processToInterrupt){
        ProcessView processView = findProcessView(processToInterrupt,runningList);
        runningList.remove(processView);
        readyList.add(processView);
    }
    public void submitProcess(Process processToSubmit){
        ProcessView processView = findProcessView(processToSubmit,createdList);
        if (processView == null){
            System.out.println("Em dispatchProcess is true ou processview e nulo");
        }
        createdList.remove(processView);
        readyList.add(processView);
    }
    public void finishProcess(Process processToFinish) {
        ProcessView processView = findProcessView(processToFinish,runningList);
        runningList.remove(processView);
        finishedList.add(processView);
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
        ProcessView processView = findProcessView(object,runningList);
        if (processView != null){
            processView.setExecutionProgress(progress);
        }
    }
}