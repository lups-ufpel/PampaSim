package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import java.util.ArrayList;
import lombok.Getter;

import org.pampasim.filesystem.directory.*;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.FileSystemSimulation;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import org.pampasim.filesystem.viewmodel.SimulationClockListener;

@Getter
public class DirectoryViewModel extends SimulationClockListener implements ViewModel {
  private FileSystemSimulation fileSystemSimulation;
  private FileSystem fileSystem;
  private ObservableList<DirectoryEntry> observableEntries;
  private String path;

  public DirectoryViewModel(FileSystemSimulation fileSystemSimulation, String path){
    this.fileSystemSimulation = fileSystemSimulation;
    this.fileSystem = fileSystemSimulation.getFileSystem();
    this.path = path;
    this.observableEntries = FXCollections.observableArrayList(Directory.find(fileSystem, path).getEntries());
    refreshEntries();
    addSimulationClockListener(fileSystemSimulation);
  }

  public void refreshEntries() {
      observableEntries.setAll(
          Directory.find(fileSystem, path).getEntries()
      );
  }

  @Override
  protected void onSimulationTick(){
    refreshEntries();
  }

  public ObservableList<DirectoryEntry> getEntries(){
    return observableEntries;
  }

  public FileSystem getFileSystem() {
      return fileSystem;
  }

  public String getFilePath(int index){
    String filePath = "";
    if(path.equals("/")){
      filePath = path + getEntries().get(index).getName();
    }  else {
      filePath = path + "/" + getEntries().get(index).getName(); 
    }

    return filePath;

  }
}
