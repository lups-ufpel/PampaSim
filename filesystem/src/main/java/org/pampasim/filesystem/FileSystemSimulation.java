package org.pampasim.filesystem;

import lombok.Getter;
import java.util.List;
import javafx.collections.ObservableList;

import org.pampasim.core.SimulationBase;
import org.pampasim.core.events.Event;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.core.AllocationScheme;
import org.pampasim.filesystem.core.BlockRecord;

@Getter
public class FileSystemSimulation extends SimulationBase {

  private Disk disk;
  private FileSystem fileSystem;

  public FileSystemSimulation(SimulationBase parent){
    super(parent);

    // register handlers
    

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

    @Override
    public void acceptEvent(Event evt) {}

    public void incrementWaitingTimes() {}


}
