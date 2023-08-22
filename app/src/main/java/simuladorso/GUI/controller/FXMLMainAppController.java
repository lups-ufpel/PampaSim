package simuladorso.GUI.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.Messager;
import simuladorso.MessageBroker.MessageBroker;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class FXMLMainAppController implements Initializable, Messager {
    @FXML
    Pane cpusPane;
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
    MessageBroker messageBroker;
    Logger logger;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.messageBroker = MessageBroker.getInstance();
    }

    public boolean showFXMLCreateProcessDialog(ArrayList<String> params) throws IOException {
        System.out.println("Clicked");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/createProcessDialog.fxml"));
        AnchorPane dialog = (AnchorPane) loader.load();

        Stage stage = new Stage();
        stage.setTitle("Create new process");
        Scene scene = new Scene(dialog);
        stage.setScene(scene);

        FXMLCreateProcessDialogController controller = loader.getController();
        controller.setDialogStage(stage);
        controller.setParams(params);

        stage.showAndWait();

        return controller.isCreateBtnClicked();
    }

    public void handleCreateProcessBtn(ActionEvent actionEvent) throws IOException {
        ArrayList<String> params = new ArrayList<>();
        boolean buttonConfirmarClicked = showFXMLCreateProcessDialog(params);
        if (buttonConfirmarClicked) {
            System.out.println("OK");
        }

    }

    public void handleRemoveProcessBtn(ActionEvent actionEvent) throws IOException {
        throw new IOException();
    }

    public void handleExitMenuButton(ActionEvent actionEvent) throws RuntimeException {
        this.mainStage.close();
    }

    public void handleStartVMMenuButton(ActionEvent actionEvent) {
        Message msg = new Message("START_VM");
        this.messageBroker.handleMessage(msg);
    }

    public void handleStopVMMenuButton(ActionEvent actionEvent) throws Exception {
        Message msg = new Message("STOP_VM");
        msg.setSender(this);
        this.messageBroker.handleMessage(msg);
    }

    public void handleRunningProcessMenuButton(ActionEvent actionEvent) {
        Message msg = new Message("LIST_RUNNING_PROCESSES");
        msg.setSender(this);
        this.messageBroker.handleMessage(msg);
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void receive(Object object) {
        LinkedList<Integer> pids = (LinkedList<Integer>) object;
        System.out.println(pids);
    }
}
