package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import java.util.List;
import java.util.Collections;
import javafx.collections.ObservableList;
import lombok.Getter;
import javafx.collections.FXCollections;

import org.pampasim.filesystem.FileSystemSimulation;
import org.pampasim.filesystem.core.BlockRecord;


public class FileSystemTabViewModel implements ViewModel {
  private FileSystemSimulation fileSystemSimulation;
  @Getter private final ObservableList<BlockViewModel> observableBlockViewModels = FXCollections.observableArrayList();
  //
      public FileSystemTabViewModel(
            FileSystemSimulation fileSystemSimulation
            //ObservableList<ProcessViewModel> observableProcessList,
            ) {

        this.fileSystemSimulation = fileSystemSimulation;
        List<BlockRecord> blockRecordsList = fileSystemSimulation.getBlockRecordsReference();

        createObservableViewModels(blockRecordsList, observableBlockViewModels);

    }

    // similar to createFrameList on memorytabviewmodel
    private void createObservableViewModels(List<BlockRecord> blockRecordsList, ObservableList<BlockViewModel> observableList) {
        for(int i = 0; i < blockRecordsList.size(); i++){
            BlockRecord entry = blockRecordsList.get(i);
            BlockViewModel vm = new BlockViewModel(i, entry.type().toString(), entry.type());

            vm.typeProperty().set(entry.type());

            observableList.add(vm);
        }
    }

}
