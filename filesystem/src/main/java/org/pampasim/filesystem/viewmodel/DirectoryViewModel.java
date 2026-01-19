package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import java.util.ArrayList;
import lombok.Getter;

import org.pampasim.filesystem.directory.*;
import org.pampasim.filesystem.core.FileSystem;

@Getter
public class DirectoryViewModel implements ViewModel {
  private FileSystem fileSystem;
  private String path;

  public DirectoryViewModel(FileSystem fileSystem, String path){
    this.fileSystem = fileSystem;
    this.path = path;
  }

  public ArrayList<DirectoryEntry> getEntries(){
    return Directory.find(fileSystem, path).getEntries();
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
