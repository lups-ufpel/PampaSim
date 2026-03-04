package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.pampasim.filesystem.FileSystemSimulation;
import org.pampasim.filesystem.view.FATEntry;
import org.pampasim.filesystem.viewmodel.SimulationClockListener;
import lombok.Getter;

public class FATViewModel extends SimulationClockListener implements ViewModel {

    @Getter private FileSystemSimulation fileSystemSimulation;

    private final ObservableList<FATEntry> fatEntries =
            FXCollections.observableArrayList();

    public FATViewModel(FileSystemSimulation fileSystemSimulation) {
      this.fileSystemSimulation = fileSystemSimulation;

      loadFAT(fileSystemSimulation.getFileSystem().getFileAllocationTable());
      addSimulationClockListener(fileSystemSimulation);
    }

    @Override
    protected void onSimulationTick(){
      loadFAT(fileSystemSimulation.getFileSystem().getFileAllocationTable());
    }

    public FileSystemSimulation getFileSystemSimulation(){
      return fileSystemSimulation;
    }


    public ObservableList<FATEntry> getFatEntries() {
        return fatEntries;
    }

    public void loadFAT(int[] fat) {
        fatEntries.clear();

        for (int i = 0; i < fat.length; i++) {
            fatEntries.add(new FATEntry(i, fat[i]));
        }
    }
}
