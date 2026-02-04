package org.pampasim.filesystem;

import lombok.Getter;
import java.util.List;
import javafx.collections.ObservableList;

import org.pampasim.core.SimulationBase;
import org.pampasim.core.events.Event;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.Partition;
import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.resources.filesystem.config.FileSystemConfig;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.file.File;
import org.pampasim.filesystem.FileSystemEventManager;
import org.pampasim.events.FileSystem.*;

@Getter
public class FileSystemSimulation extends SimulationBase {

  private Disk disk;
  private FileSystem fileSystem;

  public FileSystemSimulation(SimulationBase parent){
    super(parent);

    this.setEventManager(new FileSystemEventManager(this));

    // register handlers
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.CreateFile.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.DeleteFile.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.OpenFile.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.CloseFile.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.ReadFile.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.WriteFile.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.CreateDirectory.class, this);
    
    parent.getEventManager().addEventHandler(
            org.pampasim.events.FileSystem.DeleteDirectory.class, this);

    // initialize subsystems
    try{
      this.disk = new Disk(FileSystemConfig.getBlockSizeBytes(), FileSystemConfig.getNumberOfBlocks());
    }  catch (IllegalArgumentException e){
      // TODO: add alert
      throw e;
    }
    
    boolean active = true;
    // only one partition for now
    Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex(), active);
    Partition[] partitions = {partitionA};
    disk.setPartitions(partitions);

    this.fileSystem = new FileSystem(this, disk, partitionA, FileSystemConfig.getAllocationScheme());
    // maybe should be on first tick?
    fileSystem.initialize();

  }

  public ObservableList<BlockRecord> getBlockRecordsReference(){
    return disk.getBlockRecords();
  }

  public void acceptEvent(Event event) {
    super.acceptEvent(event);
  }

  
  
  public void incrementWaitingTimes() {}


}
