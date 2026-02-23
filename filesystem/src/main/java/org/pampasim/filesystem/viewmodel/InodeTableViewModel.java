package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import org.pampasim.filesystem.viewmodel.SimulationClockListener;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.FileSystemSimulation;

@Getter
public class InodeTableViewModel extends SimulationClockListener implements ViewModel {
    private final FileSystemSimulation fileSystemSimulation;
    private final FileSystem fileSystem;
    @Getter private final ObservableList<InodeBlockViewModel> inodeBlockViewModels = FXCollections.observableArrayList();

    public InodeTableViewModel(FileSystemSimulation fileSystemSimulation) {
        this.fileSystemSimulation = fileSystemSimulation;
        this.fileSystem = fileSystemSimulation.getFileSystem();

        refreshViewModels();
        addSimulationClockListener(fileSystemSimulation);
    }

    private void refreshViewModels() {
        inodeBlockViewModels.clear();
        for(int i = 0; i < fileSystem.getNumberOfInodes(); i++){
            InodeBlockViewModel vm = new InodeBlockViewModel(fileSystemSimulation, i);
            inodeBlockViewModels.add(vm);
        }
    }

    @Override
    protected void onSimulationTick(){
          refreshViewModels();
    }

    public Inode[] getInodeTable(){
      return fileSystem.getInodeTable();
    }
}
