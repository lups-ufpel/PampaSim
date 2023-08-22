package simuladorso;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import simuladorso.GUI.Functions;
import simuladorso.GUI.SimulatorGui;
import simuladorso.Logger.LogType;
import simuladorso.Logger.Logger;
import simuladorso.MessageBroker.MessageBroker;
import simuladorso.Utils.Observer;
import simuladorso.VirtualMachine.VirtualMachine;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();

        Logger.getInstance().subscribe(cli);

        MessageBroker mb = MessageBroker.getInstance();
        //Logger mbLogger = mb.getLogger();
        //mbLogger.setLogLevel(LogType.DEBUG);
        //mbLogger.subscribe(cli);

        SimulatorGui gui = new SimulatorGui();
        //Logger guiLogger = gui.getLogger();
        //guiLogger.setLogLevel(LogType.DEBUG);
        //guiLogger.subscribe(cli);

        VirtualMachine vm = new VirtualMachine(4);
        //Logger vmLogger = vm.getLogger();
        //vmLogger.setLogLevel(LogType.DEBUG);
        //vmLogger.subscribe(cli);

        mb.setVm(vm);

        Thread vmThread = new Thread(vm);
        vmThread.setDaemon(true);
        vmThread.start();

        gui.run(args);
    }
}