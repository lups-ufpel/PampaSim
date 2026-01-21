package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;

import org.pampasim.filesystem.core.FileSystem;

@Getter
public class InodeBlockViewModel implements ViewModel {
    private final FileSystem fileSystem;
    private final int index;

    public InodeBlockViewModel(FileSystem fileSystem, int index) {
        this.fileSystem = fileSystem;
        this.index = index;
    }

    public Color getCorrespondingColor(int index){
      return fileSystem.isInodeEmpty(index)
              ? Color.web("#B0BEC5")    // light gray
              : Color.web("#4CAF50");   // soft green
    }

}
