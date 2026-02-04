package org.pampasim.filesystem.file;

import org.pampasim.filesystem.core.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.mapping.*;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;

import java.time.Instant;

public class File{

  public static FileMapping createContiguous(
          FileSystem fileSystem,
          String path,
          int finalSizeBlocks,
          boolean isDirectory
  ) {
      if(fileSystem.getAllocationScheme() != AllocationScheme.CONTIGUOUS){
        throw new IllegalStateException("Wrong function called for creating file.");

      }
      if (finalSizeBlocks > fileSystem.freeBlocksCount()) {
          throw new Error(
                  "file is too big. File system has " +
                  fileSystem.freeBlocksCount() + " free blocks."
          );
      }
  
      FileMetadata metadata =
              new FileMetadata(0, isDirectory, Instant.now());
  
      int firstBlockIndex =
              fileSystem.getFreeBlocksAndSetAllocated(finalSizeBlocks);
  
      FileMapping mapping =
              new ContiguousMapping(firstBlockIndex, finalSizeBlocks, metadata);
  
      BlockType type = (isDirectory) ? BlockType.DIRECTORY : BlockType.FILE;
      fileSystem.setBlockRecord(firstBlockIndex, firstBlockIndex + finalSizeBlocks, type, path);
      addToDirectory(fileSystem, path, mapping);
  
      return mapping;
  }

  public static FileMapping createInodes(
          FileSystem fileSystem,
          String path,
          boolean isDirectory
  ) {

      if(fileSystem.getAllocationScheme() != AllocationScheme.INODES){
        throw new IllegalStateException("Wrong function called for creating file.");
      }

      FileMetadata metadata =
              new FileMetadata(0, isDirectory, Instant.now());
  
      FileMapping mapping =
              new InodeMapping(fileSystem.nextFreeInode());
  
      addToDirectory(fileSystem, path, mapping);
  
      // must happen after directory entry exists
      metadata.writeToDisk(fileSystem, path);
  
      return mapping;
  }

  public static FileMapping createFAT(
          FileSystem fileSystem,
          String path,
          boolean isDirectory
  ) {

      if(fileSystem.getAllocationScheme() != AllocationScheme.FAT){
        throw new IllegalStateException("Wrong function called for creating file.");
      }
      FileMetadata metadata =
              new FileMetadata(0, isDirectory, Instant.now());
  
      FileMapping mapping =
              new FATMapping(FATMapping.FIRST_BLOCK_NOT_SET, metadata);
  
      addToDirectory(fileSystem, path, mapping);
  
      return mapping;
  }

  private static void addToDirectory(
          FileSystem fileSystem,
          String path,
          FileMapping mapping
  ) {
      String[] segments = path.split("/");
      String name = segments[segments.length - 1];
      Directory dir = Directory.findParent(fileSystem, path);
      dir.addEntry(new DirectoryEntry(name, mapping));
  }

  public static void write(FileSystem fileSystem, String path, byte[] data, int position){
    fileSystem.writeToFile(path, data, position);
  }

  public static byte[] read(FileSystem fileSystem, String path, int byteNumber, int position){
    return fileSystem.readFromFile(path, byteNumber, position);
  }

}
