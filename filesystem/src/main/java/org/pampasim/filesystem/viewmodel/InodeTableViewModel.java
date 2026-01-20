package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.inode.Inode;

@Getter
public class InodeTableViewModel implements ViewModel {
    private final FileSystem fileSystem;
    @Getter private final InodeBlockViewModel[] inodeBlockViewModels;

    public InodeTableViewModel(FileSystem fileSystem) {
        this.fileSystem = fileSystem;
        inodeBlockViewModels = new InodeBlockViewModel[fileSystem.getNumberOfInodes()];
        createInodeBlockViewModels();
    }

    private void createInodeBlockViewModels() {
        for(int i = 0; i < inodeBlockViewModels.length; i++){
            InodeBlockViewModel vm = new InodeBlockViewModel(fileSystem, i);
            inodeBlockViewModels[i] = vm;
        }
    }

    public Inode[] getInodeTable(){
      return fileSystem.getInodeTable();
    }
}
