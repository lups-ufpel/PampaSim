package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;

import org.pampasim.filesystem.core.AllocationBitMap;
import org.pampasim.filesystem.viewmodel.BitViewModel;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

// identical to FreeInodes... for now?
public class FreeBlocksViewModel implements ViewModel {

  private ObservableList<BitViewModel> bitViewModels = FXCollections.observableArrayList();
  private AllocationBitMap freeBlocksBitMap;

  public FreeBlocksViewModel(AllocationBitMap freeBlocksBitMap){
    this.freeBlocksBitMap = freeBlocksBitMap;

    for(int i = 0; i < freeBlocksBitMap.length(); i++){
      Boolean entry = freeBlocksBitMap.get(i);
      BitViewModel vm = new BitViewModel(i, entry);

      bitViewModels.add(vm);
    }
      

    freeBlocksBitMap.versionProperty().addListener((obs, oldV, newV) -> {
      for(int i = 0; i < bitViewModels.size(); i++){
        bitViewModels.get(i).setBit(this.freeBlocksBitMap.get(i));
      }
        
    });
  }

  public ObservableList<BitViewModel> getBitViewModels(){
    return bitViewModels;
  }

}
