package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.FileSystemSimulation;

@Getter
public class InodeTableViewModel implements ViewModel {
    private final FileSystemSimulation fileSystemSimulation;
    private final FileSystem fileSystem;
    private final ObservableList<BlockRecord> observableBlockRecords;
    @Getter private final ObservableList<InodeBlockViewModel> inodeBlockViewModels = FXCollections.observableArrayList();

    public InodeTableViewModel(FileSystemSimulation fileSystemSimulation) {
        this.fileSystemSimulation = fileSystemSimulation;
        this.fileSystem = fileSystemSimulation.getFileSystem();
        this.observableBlockRecords = fileSystem.getBlockRecordsReference();

        refreshViewModels();
        addListener();
    }

    private void refreshViewModels() {
        inodeBlockViewModels.clear();
        for(int i = 0; i < fileSystem.getNumberOfInodes(); i++){
            InodeBlockViewModel vm = new InodeBlockViewModel(fileSystemSimulation, i);
            inodeBlockViewModels.add(vm);
        }
    }

    // listen to totalWrites instead of block change
    private void addListener(){
        observableBlockRecords.addListener((ListChangeListener<BlockRecord>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                  refreshViewModels();
                }
            }
        });

    }

    public Inode[] getInodeTable(){
      return fileSystem.getInodeTable();
    }
}
