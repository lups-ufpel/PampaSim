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
import simuladorso.GUI.model.CoreInfo;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.Message;
import simuladorso.MessageBroker.MessageBroker;
import simuladorso.MessageBroker.MessageType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class FXMLMainAppController implements Initializable {
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
        controller.setDialogStage(stage);
        controller.setParams(params);

        stage.showAndWait();

        return controller.isCreateBtnClicked();
    }

    public void handleCreateProcessBtn(ActionEvent actionEvent) throws IOException {
        List<String> params = new ArrayList<>();
        boolean buttonConfirmarClicked = showFXMLCreateProcessDialog(params);
        if (buttonConfirmarClicked) {
            messageBroker.invoke(MessageType.KERNEL_NEW_PROCESS);
        }

    }

    public void handleRemoveProcessBtn(ActionEvent actionEvent) throws IOException {
        throw new IOException();
    }

    public void handleExitMenuButton(ActionEvent actionEvent) throws RuntimeException {
        this.mainStage.close();
    }

    public void handleStartVMMenuButton(ActionEvent actionEvent) {
        this.messageBroker.invoke(MessageType.START_VM);
    }

    public void handleStopVMMenuButton(ActionEvent actionEvent) throws Exception {
        this.messageBroker.invoke(MessageType.STOP_VM);
    }

    public void handleRunningProcessMenuButton(ActionEvent actionEvent) {
        LinkedList<Integer> pids = new LinkedList<>();
        try {
            pids.addAll((LinkedList<Integer>) this.messageBroker.invoke(MessageType.LIST_RUNNING_PROCESSES));
        } catch (Exception e) {
            Logger.getInstance().error("Couldn't cast running processes pids");
        }

        System.out.println(pids);
    }

    public void handleRefreshCoresMenuButton(ActionEvent actionEvent) {
        LinkedList<CoreInfo> info = new LinkedList<>();

        /*
        try {
            info.addAll(this.messageBroker.invoke(MessageType.LIST_CORES));
        } catch (Exception e) {
            Logger.getInstance().error(e.getMessage());
        }

        refreshCoresPane(info);

         */
    }

    public void refreshCoresPane(LinkedList<CoreInfo> info) {
        this.cpusPane.getChildren().clear();
    }

    private void loadCpusPane() throws IOException {
        int numCores = (Integer) messageBroker.invoke(MessageType.GET_NUM_CORES);
        for (int i = 0; i < numCores; i++) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(this.getClass().getResource("fxml/cpuContainer.fxml"));
            AnchorPane cpuContainer = loader.load();
            FXMLCpuContainerController controller = loader.getController();
            controller.setData(i, false, 0);
        }
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
