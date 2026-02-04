package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import java.util.List;
import java.util.Collections;
import javafx.collections.ObservableList;
import lombok.Getter;
import javafx.collections.FXCollections;
import javafx.beans.InvalidationListener;

import org.pampasim.filesystem.FileSystemSimulation;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.LegendEntry;


public class FileSystemTabViewModel implements ViewModel {
  private FileSystemSimulation fileSystemSimulation;
  private final ObservableList<BlockRecord> observableBlockRecords;
  @Getter private final ObservableList<BlockViewModel> observableBlockViewModels = FXCollections.observableArrayList();
  private final ObservableList<LegendEntry> legendEntries = FXCollections.observableArrayList();

      public FileSystemTabViewModel(
            FileSystemSimulation fileSystemSimulation
            //ObservableList<ProcessViewModel> observableProcessList,
            ) {

        this.fileSystemSimulation = fileSystemSimulation;
        this.observableBlockRecords = fileSystemSimulation.getBlockRecordsReference();

        createObservableViewModels();
        refreshLegendEntries();
        addDiskWriteListeners(fileSystemSimulation.getDisk());
    }

    // similar to createFrameList on memorytabviewmodel
    private void createObservableViewModels() {
        for(int i = 0; i < observableBlockRecords.size(); i++){
            BlockRecord entry = observableBlockRecords.get(i);
            BlockViewModel vm = new BlockViewModel(i, entry.type().toString(), entry, fileSystemSimulation);

            observableBlockViewModels.add(vm);
        }
    }

    public void addDiskWriteListeners(Disk disk){
        disk.getTotalWritesProperty().addListener((InvalidationListener) obs -> {
            refreshViewModels();
            refreshLegendEntries();
        });
    }

    private void refreshViewModels(){
      for(int i = 0; i < observableBlockViewModels.size(); i++){
        observableBlockViewModels.get(i).setBlockRecord(observableBlockRecords.get(i));
      }
    }

    public void refreshLegendEntries(){
      legendEntries.clear();
      legendEntries.addAll(fileSystemSimulation.getFileSystem().getFileTreeLegendEntries());

    }

    public ObservableList<LegendEntry> getLegendEntries() {
        return legendEntries;
    }

}
