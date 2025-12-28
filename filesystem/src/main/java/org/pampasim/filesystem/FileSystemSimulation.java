package org.pampasim.filesystem;

import lombok.Getter;
import java.util.List;

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
  
    int blockSizeBytes;
    int numberOfBlocks;
    AllocationScheme allocationScheme;

    if(FileSystemConfig.isInitialized()){
      blockSizeBytes = FileSystemConfig.getBlockSizeBytes();
      numberOfBlocks = FileSystemConfig.getNumberOfBlocks();
      allocationScheme = FileSystemConfig.getAllocationScheme();
    }  else{
      // if config has not been set, use default values (prevents crash)
      blockSizeBytes = 64;
      numberOfBlocks = 512;
      allocationScheme = AllocationScheme.CONTIGUOUS;
    }

    try{
      this.disk = new Disk(blockSizeBytes, numberOfBlocks);
    }  catch (IllegalArgumentException e){
      // TODO: add alert
      throw e;
    }
    
    boolean active = true;
    // only one partition for now
    Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex(), active);
    Partition[] partitions = {partitionA};
    disk.setPartitions(partitions);

    this.fileSystem = new FileSystem(disk, partitionA, allocationScheme);
    // maybe should be on first tick?
    fileSystem.initialize();

  }

    public List<BlockRecord> getBlockRecordsReference(){
      return disk.getBlockRecords();
    }

    @Override
    public void acceptEvent(Event evt) {}

    public void incrementWaitingTimes() {}


}
