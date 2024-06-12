package org.pampasim.GUI.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.pampasim.Mediator.Mediator;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLMainAppController implements Initializable {
    @FXML
    HBox cpusBox;
    @FXML
    Pane queuesPane;
    @FXML
    Pane processInfoPane;
    @FXML
    Button createProcessBtn;

    // Sliders
    @FXML
    Slider clockSlider;
    @FXML
    Slider quantumSlider;
    @FXML
    Slider waitTimeSlider;
    @FXML
    MenuItem exitMenuItem;

    // Buttons
    @FXML
    Button deleteProcessBtn;

    Stage mainStage;
    int selectedProcessPid;
    Mediator mediator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public boolean showFXMLCreateProcessDialog(List<String> params) throws IOException {
        System.out.println("Clicked");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/createProcessDialog.fxml"));
        AnchorPane dialog = (AnchorPane) loader.load();

        Stage stage = new Stage();
        stage.setTitle("Create new process");
        Scene scene = new Scene(dialog);
        stage.setScene(scene);

        FXMLCreateProcessDialogController controller = loader.getController();
        controller.setMediator(this.mediator);
        controller.setDialogStage(stage);
        controller.setParams(params);

        stage.showAndWait();

        return controller.isCreateBtnClicked();
    }

    public void handleCreateProcessBtn(ActionEvent actionEvent) throws IOException {
        List<String> params = new ArrayList<>();
        boolean buttonConfirmarClicked = showFXMLCreateProcessDialog(params);
        if (buttonConfirmarClicked) {
            this.mediator.invoke(Mediator.Action.KERNEL_NEW_PROCESS);
        }
    }

    public void handleRemoveProcessBtn(ActionEvent actionEvent) throws IOException {
        throw new IOException();
    }

    public void handleExitMenuButton(ActionEvent actionEvent) throws RuntimeException {
        this.mainStage.close();
    }

    public void handleStartVMMenuButton(ActionEvent actionEvent) {
        this.mediator.invoke(Mediator.Action.START_VM);
    }

    public void handleStopVMMenuButton(ActionEvent actionEvent) throws Exception {
        this.mediator.invoke(Mediator.Action.STOP_VM);
    }

    public void handleRunningProcessMenuButton(ActionEvent actionEvent) throws IOException {

    }

    public void handleRefreshCoresMenuButton(ActionEvent actionEvent) throws IOException {
        this.refreshCoresPane();
    }

    public void refreshCoresPane() throws IOException {
        Integer[] pids = (Integer[]) this.mediator.invoke(Mediator.Action.LIST_RUNNING_PROCESSES);

        this.cpusBox.getChildren().clear();

        for (int i = 0; i < pids.length; i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("/fxml/cpuContainer.fxml"));
            Pane cpuContainer = loader.load();
            FXMLCoreContainerController controller = loader.getController();
            if (pids[i] != null)
                controller.setData(i, true, pids[i]);
            else
                controller.setData(i, false, 0);

            this.cpusBox.getChildren().add(cpuContainer);
        }
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setMediator(Mediator mediator) {
        this.mediator = mediator;
    }
}
