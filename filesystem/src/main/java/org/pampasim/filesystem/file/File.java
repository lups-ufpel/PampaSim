package org.pampasim.filesystem.file;

import org.pampasim.filesystem.core.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.mapping.FileMapping;
import org.pampasim.filesystem.mapping.ContiguousMapping;
import org.pampasim.filesystem.mapping.InodeMapping;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;

import java.time.Instant;

public class File{
  // could have attributes fileSystem and path for ease of use?

  public static FileMapping create(FileSystem fileSystem, String path, int currentSizeBytes, int finalSizeBlocks, boolean isBinary, boolean isDirectory){
    if(finalSizeBlocks > fileSystem.freeBlocksCount()){
      throw new Error("file is too big. File system has " + fileSystem.freeBlocksCount() + " free blocks.");
    }

    FileMetadata metadata = new FileMetadata(currentSizeBytes, isBinary, isDirectory, Instant.now());
    FileMapping mapping;
    switch(fileSystem.getAllocationScheme()){
      case CONTIGUOUS:
      {
        if(finalSizeBlocks == -1){
          throw new Error("incorrect create function called for contiguous file.");
        }
        int firstBlockIndex = fileSystem.getFreeBlocksAndSetAllocated(finalSizeBlocks);
        mapping = new ContiguousMapping(firstBlockIndex, finalSizeBlocks, metadata);
        break;
      }
      case INODES:
      {
        mapping = new InodeMapping(fileSystem.nextFreeInode());
        break;
      }
      case FAT:
      {
        mapping = new FATMapping(FIRST_BLOCK_NOT_SET);
        break;
      }
      default:
        throw new Error("unhandled");
    }

    String[] segments = path.split("/");
    String name = segments[segments.length - 1];
    Directory dir = Directory.findParent(fileSystem, path);
    DirectoryEntry fileEntry = new DirectoryEntry(name, mapping);
    dir.addEntry(fileEntry);
    if(fileSystem.getAllocationScheme() == AllocationScheme.INODES){
        //needs to be done after addEntry
        // metadata for others have to be written later
        metadata.writeToDisk(fileSystem, path);
    }

    return mapping;
  }

  public static FileMapping create(FileSystem fileSystem, String path, int currentSizeBytes, boolean isBinary, boolean isDirectory){
    return create(fileSystem, path, currentSizeBytes, -1, isBinary, isDirectory);
  }

  public static FileMapping create(FileSystem fileSystem, String path, boolean isBinary, boolean isDirectory){
    return create(fileSystem, path, 0, -1, isBinary, isDirectory);
  }

  public static void write(FileSystem fileSystem, String path, byte[] data, int position){
    fileSystem.writeToFile(path, data, position);
  }

  public static byte[] read(FileSystem fileSystem, String path, int byteNumber, int position){
    return fileSystem.readFromFile(path, byteNumber, position);
  }

}
