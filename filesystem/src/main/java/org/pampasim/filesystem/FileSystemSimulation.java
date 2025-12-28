package org.pampasim.filesystem;

import lombok.Getter;
import org.pampasim.core.SimulationBase;
import org.pampasim.core.events.Event;

//import org.pampasim.events.Process.IoOperation;
//import org.pampasim.events....;

import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.core.AllocationScheme;

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
    // if config has not been set, use default (prevents crash)
    if(FileSystemConfig.getNumberOfBlocks() == 0){
      blockSizeBytes = 64;
      numberOfBlocks = 512;
      allocationScheme = AllocationScheme.CONTIGUOUS;
    }  else{
      blockSizeBytes = FileSystemConfig.getBlockSizeBytes();
      numberOfBlocks = FileSystemConfig.getNumberOfBlocks();
      allocationScheme = FileSystemConfig.getAllocationScheme();
    }

    this.disk = new Disk(blockSizeBytes, numberOfBlocks);
    
    boolean active = true;
    // only one partition for now
    Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex(), active);
    Partition[] partitions = {partitionA};
    disk.setPartitions(partitions);

    this.fileSystem = new FileSystem(disk, partitionA, allocationScheme);
    // maybe should be on first tick?
    fileSystem.initialize();

  }

    @Override
    public void acceptEvent(Event evt) {}

    public void incrementWaitingTimes() {}


}
