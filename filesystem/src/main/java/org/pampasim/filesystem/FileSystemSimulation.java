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
import org.pampasim.filesystem.file.File;
import org.pampasim.filesystem.FileSystemEventManager;
import org.pampasim.events.FileSystem.*;

@Getter
public class FileSystemSimulation extends SimulationBase {

  private Disk disk;
  private FileSystem fileSystem;

  public FileSystemSimulation(SimulationBase parent){
    super(parent);

    // register handlers
    parent.getEventManager().addEventHandler(CreateFile.class, this);

    this.setEventManager(new FileSystemEventManager(this));

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

  public void processEvent(Event event) {
      switch (event) {
          case CreateFile e -> System.exit(31); //File.create(fileSystem,  ((CreateFile) event).getString(), 0, -1, false, false);
          default -> throw new IllegalStateException(
                  "[PhysicalMemory] Evento do tipo " + event.getClass().getSimpleName()
                          + " não pode ser tratado, evento serial: " + event.getSerial()
          );
      }

  }
  
  public void incrementWaitingTimes() {}


}
