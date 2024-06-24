package org.pampasim.gui.model;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.Os;
import org.pampasim.Os.PidAllocator;
import org.pampasim.Os.Process;
import org.pampasim.Os.Scheduler;
import org.pampasim.gui.ProcessScope;
import org.pampasim.gui.listeners.ProcessEventInfo;

import java.util.Optional;

public class SimulationViewModel implements ViewModel {
    public final static String NEW_PROCESS = "NEW_PROCESS";
    public final static String START_PROCESS = "START_PROCESS";
    public final static String FINISH_PROCESS = "FINISH_PROCESS";
    public final static String RUN_PROCESS = "RUN_PROCESS";

    @InjectScope
    private ProcessScope processScope;
    public SimulationViewModel() {
        Mediator.getInstance().registerComponent(this, Mediator.Component.GUI);
    }
    public ProcessScope getProcessScope() {
        return processScope;
    }
    public void createNewProcess() {
       var burst = processScope.getBurstProperty().getValue();
       var priority = processScope.getDurationProperty().getValue();
       var arrival = processScope.getDurationProperty().getValue();
       var newProcess = (ProcessMock) Mediator.getInstance().getOs().createProcess(Process.Type.SIMPLE,burst,priority,arrival);
       addProcessListeners(newProcess);
       newProcess.notifyOnCreate();
    }
    public void addProcessListeners(ProcessMock processMock) {
        processMock.addOnCreateListener(this::notifyGuiOnCreatedProcess);
        processMock.addOnStartListener(this::notifyGuiOnStartProcess);
        processMock.addOnFinishListener(this::notifyGuiOnFinishedProcess);
        processMock.addOnUpdateListener(this::notifyGuiOnExecuteProcess);
    }
    private void notifyGuiOnExecuteProcess(ProcessEventInfo eventInfo) {
       this.publish(RUN_PROCESS);
    }
    private void notifyGuiOnFinishedProcess(ProcessEventInfo eventInfo) {
       this.publish(FINISH_PROCESS);
    }
    private void notifyGuiOnStartProcess(ProcessEventInfo eventInfo) {
        this.publish(START_PROCESS);
    }
    private void notifyGuiOnCreatedProcess(ProcessEventInfo eventInfo) {
        this.publish(NEW_PROCESS);
    }
    /**
     * --- OLDER METHODS THAT USE MEDIATOR GOES BELOW  \:/
     */
    public boolean schedulerDialog() {
        Dialog<ButtonType> schedulerChoiceDialog = new SchedulerChoiceDialog();
        Optional<ButtonType> schedulerChoiceDialogResult = schedulerChoiceDialog.showAndWait();
        if (schedulerChoiceDialogResult.isPresent() && schedulerChoiceDialogResult.get() == ButtonType.OK) {
            System.out.println("Scheduler escolhido");
            Scheduler scheduler = Mediator.getInstance().getScheduler();
            Os kernel = (Os) Mediator.getInstance().retrieveComponent(Mediator.Component.KERNEL);
            kernel.dispatchAll(scheduler);
        }
        //FIX LINE BELOW
        return schedulerChoiceDialogResult.isPresent() && schedulerChoiceDialogResult.get() == ButtonType.OK;
    }
    public void runSimulation() {
        Mediator.getInstance().send(this, Mediator.Action.RUN);
    }
}