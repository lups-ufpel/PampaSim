package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;

import org.pampasim.filesystem.core.AllocationBitMap;
import org.pampasim.filesystem.viewmodel.BitViewModel;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class FreeInodesViewModel implements ViewModel {

  private ObservableList<BitViewModel> bitViewModels = FXCollections.observableArrayList();
  private AllocationBitMap freeInodesBitMap;

  public FreeInodesViewModel(AllocationBitMap freeInodesBitMap){
    this.freeInodesBitMap = freeInodesBitMap;

    for(int i = 0; i < freeInodesBitMap.length(); i++){
      Boolean entry = freeInodesBitMap.get(i);
      BitViewModel vm = new BitViewModel(i, entry, 0);

      bitViewModels.add(vm);
    }
      

    freeInodesBitMap.versionProperty().addListener((obs, oldV, newV) -> {
      for(int i = 0; i < bitViewModels.size(); i++){
        bitViewModels.get(i).setBit(this.freeInodesBitMap.get(i));
      }
        
    });
  }

  public ObservableList<BitViewModel> getBitViewModels(){
    return bitViewModels;
  }
 
}
