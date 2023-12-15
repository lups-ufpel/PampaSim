package org.simuladorso.GUI;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.simuladorso.GUI.controller.FXMLMainAppController;
import org.simuladorso.Mediator.Mediator;

import java.io.IOException;

public class View extends AnchorPane {

    public AnchorPane root = new AnchorPane();

    public View() throws IOException{
        createView();
    }
    public void createView() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/PampaosUI.fxml"));

        if (loader.getLocation() == null)  {
            throw new IOException("FXML file not found");
        }

        try {
            this.root = loader.load();
            System.out.println("chegou aqui!");
        } catch (IOException e) {
            System.out.println("fudeu");

        }

    }
}
