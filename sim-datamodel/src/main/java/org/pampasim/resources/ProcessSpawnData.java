package org.pampasim.resources;

import java.util.List;
import org.pampasim.resources.fileops.FileSystemOperation;

public class ProcessSpawnData{
  private final Process.CreationData creationData;
  private final List<FileSystemOperation> operations;

  public ProcessSpawnData(Process.CreationData creationData, List<FileSystemOperation> operations){
    this.creationData = creationData;
    this.operations = operations;
  }
}
