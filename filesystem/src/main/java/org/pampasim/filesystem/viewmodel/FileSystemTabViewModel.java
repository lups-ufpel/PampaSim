package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import java.util.List;
import java.util.Collections;

import org.pampasim.filesystem.FileSystemSimulation;
import org.pampasim.filesystem.core.BlockRecord;


public class FileSystemTabViewModel implements ViewModel {
  private FileSystemSimulation fileSystemSimulation;
  List<BlockRecord> unmodifiableBlockRecords;
  //
      public FileSystemTabViewModel(
            FileSystemSimulation fileSystemSimulation
            //ObservableList<ProcessViewModel> observableProcessList,
            ) {

        this.fileSystemSimulation = fileSystemSimulation;
        unmodifiableBlockRecords = Collections.unmodifiableList(fileSystemSimulation.getBlockRecordsReference());
    }

    public BlockRecord getBlockInfo(int blockIndex){
      return unmodifiableBlockRecords.get(blockIndex);
    }

}
