package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;

import org.pampasim.filesystem.core.AllocationBitMap;
import org.pampasim.filesystem.viewmodel.BitViewModel;
import org.pampasim.filesystem.viewmodel.SimulationClockListener;
import org.pampasim.filesystem.FileSystemSimulation;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class FreeInodesViewModel extends SimulationClockListener implements ViewModel {

  private ObservableList<BitViewModel> bitViewModels = FXCollections.observableArrayList();
  private AllocationBitMap freeInodesBitMap;

  public FreeInodesViewModel(FileSystemSimulation fileSystemSimulation, AllocationBitMap freeInodesBitMap){
    this.freeInodesBitMap = freeInodesBitMap;

    for(int i = 0; i < freeInodesBitMap.length(); i++){
      Boolean entry = freeInodesBitMap.get(i);
      BitViewModel vm = new BitViewModel(i, entry, 0);

      bitViewModels.add(vm);
    }

    addSimulationClockListener(fileSystemSimulation);
  }


  @Override
  protected void onSimulationTick(){
    setBits();
  }

  private void setBits(){
    for(int i = 0; i < bitViewModels.size(); i++){
      bitViewModels.get(i).setBit(this.freeInodesBitMap.get(i));
    }
  }

  public ObservableList<BitViewModel> getBitViewModels(){
    return bitViewModels;
  }
 
}
