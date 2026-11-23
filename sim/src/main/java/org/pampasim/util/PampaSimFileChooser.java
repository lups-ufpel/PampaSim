package org.pampasim.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.pampasim.PampaSim;

import java.io.File;
import java.nio.file.Path;

public class PampaSimFileChooser {
    protected FileChooser fileChooser;

    public PampaSimFileChooser() {
        fileChooser = new FileChooser();
        fileChooser
            .getExtensionFilters()
            .addAll(
                    new FileChooser.ExtensionFilter(
                    "PampaSim Spec (*.xml)",
                    "*.xml"),
                    new FileChooser.ExtensionFilter(
                            "Todos (*)",
                            "*.*")
            );
    }

    public static File showSaveDialog(Stage stage) {
        PampaSimFileChooser psFChooser = new PampaSimFileChooser();
        psFChooser.fileChooser.setTitle("Salvar Spec");
        File file = psFChooser.fileChooser.showSaveDialog(stage);
        if (file == null) {
            return null;
        }
        Path fPath = file.toPath();
        // this filename mutation makes for some "interesting" issues,
        // like when the user agrees to overwrite an existing file named X,
        // but we end up overwriting X.xml. FUN!
        if (! fPath.toString().endsWith(".xml")) {
            Path newPath = Path.of(fPath + ".xml");
            File newFile = newPath.toFile();
            if (newFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Arquivo já existe");
                alert.setContentText("Arquivo com esse nome já existe, escolha outro nome.");
                alert.showAndWait();
                return showSaveDialog(stage);
            }
            return newFile;
        }
        return file;
    }

    public static File showLoadDialog() {
        PampaSimFileChooser psFChooser = new PampaSimFileChooser();
        psFChooser.fileChooser.setTitle("Carregar Spec");
        return psFChooser.fileChooser.showOpenDialog(null);
    }
}
