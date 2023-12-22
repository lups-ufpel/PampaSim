package org.simuladorso;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import org.simuladorso.GUI.ModelView;

import java.io.IOException;

public class View extends VBox {

    public VBox root = new VBox();
    public ModelView modelView = new ModelView();
    public View() throws IOException{
        createView();}
    public void createView() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/MainWindowGUI.fxml"));
        if (loader.getLocation() == null)  {
            throw new IOException("FXML file not found");
        }

        try {
            this.root.getChildren().add(loader.load());
        } catch (IOException e) {
            System.out.println("Fudeu alguma coisa");

        }

    }
}
