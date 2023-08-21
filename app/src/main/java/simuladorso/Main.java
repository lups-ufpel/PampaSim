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
import simuladorso.VirtualMachine.VirtualMachine;

import java.net.URL;

public class Main {
    public static void main(String[] args) {
        SimulatorGui gui = new SimulatorGui();
        Logger guiLogger = new Logger();
        guiLogger.setLogLevel(LogType.DEBUG);
        gui.setLogger(guiLogger);

        VirtualMachine vm = new VirtualMachine(4);
        Logger vmLogger = new Logger();
        vmLogger.setLogLevel(LogType.DEBUG);
        vm.setLogger(vmLogger);

        gui.run(args);
    }
}