package org.pampasim.filesystem;

import lombok.Getter;
import lombok.Setter;

import org.pampasim.filesystem.FileSystemConfigSelectionRecord;
import org.pampasim.filesystem.core.AllocationScheme;

abstract public class FileSystemConfig {
  @Getter private static int numberOfBlocks;
  @Getter private static int blockSizeBytes;
  @Getter private static AllocationScheme allocationScheme;
  @Getter private static boolean initialized = false;


  public static void initialize(FileSystemConfigSelectionRecord record){
    numberOfBlocks = record.numberOfBlocks();
    blockSizeBytes = record.blockSizeBytes();
    allocationScheme = switch(record.allocationScheme()){
      case "Contígua" -> AllocationScheme.CONTIGUOUS;
      case "FAT" -> AllocationScheme.FAT;
      case "I-nodes" -> AllocationScheme.INODES;
      default -> throw new IllegalArgumentException ("Allocation Scheme" + record.allocationScheme() + " does not exist.");
    };
    initialized = true;

  }

}


