package org.pampasim.util;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.pampasim.PampaSim;

import java.io.File;

public class PampaSimFileChooser {
    protected FileChooser fileChooser;

    public PampaSimFileChooser() {
        fileChooser = new FileChooser();
        fileChooser
            .getExtensionFilters()
            .add(new FileChooser.ExtensionFilter(
                    "PampaSim Spec (*.xml)",
                    "*.xml"));
    }

    public static File showSaveDialog(Stage stage) {
        PampaSimFileChooser psFChooser = new PampaSimFileChooser();
        psFChooser.fileChooser.setTitle("Salvar Spec");
        return  psFChooser.fileChooser.showSaveDialog(stage);
    }

    public static File showLoadDialog() {
        PampaSimFileChooser psFChooser = new PampaSimFileChooser();
        psFChooser.fileChooser.setTitle("Carregar Spec");
        return psFChooser.fileChooser.showOpenDialog(null);
    }
}
