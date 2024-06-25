package org.pampasim.gui.model;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.pampasim.Mediator.Mediator;
import org.pampasim.Os.*;

public class SchedulerChoiceDialog extends Dialog<ButtonType> {

    @FXML
    ChoiceBox<String> schedulerChoiceBox;
    @FXML
    CheckBox preemptionCheckBox;
    @FXML
    Spinner<Integer> quantumSpinner;

    Scheduler scheduler;
    public SchedulerChoiceDialog() {
        super();
        this.setTitle("Scheduler Choice Dialog");
        buildDialog();
        setSchedulerChoiceBox();
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                switch (schedulerChoiceBox.getValue()) {
                    case "FCFS":
                        scheduler = new SchedulerFCFS(1,Mediator.getInstance());
                        break;
                    case "SJF":
                        scheduler = new SchedulerSJF(1,Mediator.getInstance());
                        break;
                    case "Round Robin":
                        scheduler = new SchedulerRoundRobin(1,Mediator.getInstance(),quantumSpinner.getValue());
                        break;
                    case "Priority":
                        scheduler = new SchedulerPriority(1,Mediator.getInstance());
                        break;
                }
                Mediator.getInstance().registerComponent(this.scheduler,Mediator.Component.SCHEDULER);
                return ButtonType.OK;
            }
            return null;
        });
    }

    private void buildDialog() {
        FXMLLoader dialogLoader = new FXMLLoader();
        dialogLoader.setLocation(getClass().getResource("/org/pampasim/gui/view/SelectSchedulerDialogView.fxml"));
        dialogLoader.setController(this);
        try {
            GridPane content = dialogLoader.load();
            getDialogPane().setContent(content);
        } catch (Exception e) {
            System.out.println("não foi possível setar o dialog pane apartir do FXML");
            e.printStackTrace();
            // Handle IOException here
        }
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }
    private void setSchedulerChoiceBox() {
        schedulerChoiceBox.getItems().addAll("FCFS", "SJF", "Round Robin", "Priority");
        schedulerChoiceBox.setValue("FCFS");
    }
}
