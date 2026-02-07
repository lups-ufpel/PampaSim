package org.pampasim.filesystem.file;

import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.mapping.*;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;

import java.time.Instant;

// all of these methods could be on FS instead

abstract public class File{

  public static FileReference createContiguous(
          FileSystem fileSystem,
          String path,
          int currentSizeBytes,
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
              new FileMetadata(currentSizeBytes, isDirectory, Instant.now());
  
      int firstBlockIndex =
              fileSystem.getFreeBlocksAndSetAllocated(finalSizeBlocks);
  
      FileReference mapping =
              new ContiguousFileReference(firstBlockIndex, finalSizeBlocks, metadata);
  
      BlockType type = (isDirectory) ? BlockType.DIRECTORY : BlockType.FILE;
      fileSystem.setBlockRecord(firstBlockIndex, firstBlockIndex + finalSizeBlocks, type, path);
      addToDirectory(fileSystem, path, mapping);
  
      return mapping;
  }

  public static FileReference createInodes(
          FileSystem fileSystem,
          String path,
          int currentSizeBytes,
          boolean isDirectory
  ) {

      if(fileSystem.getAllocationScheme() != AllocationScheme.INODES){
        throw new IllegalStateException("Wrong function called for creating file.");
      }

      FileMetadata metadata =
              new FileMetadata(currentSizeBytes, isDirectory, Instant.now());
  
      FileReference mapping =
              new InodeFileReference(fileSystem.nextFreeInode());
  
      addToDirectory(fileSystem, path, mapping);
  
      // must happen after directory entry exists
      metadata.writeToDisk(fileSystem, path);
  
      return mapping;
  }

  public static FileReference createFAT(
          FileSystem fileSystem,
          String path,
          int currentSizeBytes,
          boolean isDirectory
  ) {

      if(fileSystem.getAllocationScheme() != AllocationScheme.FAT){
        throw new IllegalStateException("Wrong function called for creating file.");
      }
      FileMetadata metadata =
              new FileMetadata(currentSizeBytes, isDirectory, Instant.now());
  
      FileReference mapping =
              new FATFileReference(FATFileReference.FIRST_BLOCK_NOT_SET, metadata);
  
      addToDirectory(fileSystem, path, mapping);
  
      return mapping;
  }

  public static void delete(FileSystem fileSystem, String path){
    switch(fileSystem.getAllocationScheme()){
      case CONTIGUOUS -> deleteContiguous(fileSystem, path);
      case FAT -> deleteFAT(fileSystem, path);
      case INODES -> deleteInode(fileSystem, path);
      default -> throw new Error("unhandled switch case");
    }
  }

  public static void deleteContiguous(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    ContiguousFileReference mapping = (ContiguousFileReference) fileSystem.getMapping(path);
    int firstBlockIndex = mapping.getFirstBlockIndex();
    int lastBlockIndex = firstBlockIndex + mapping.getFinalSizeBlocks();

    fileSystem.freeBlocks(firstBlockIndex, lastBlockIndex);

    Directory.findParent(fileSystem, path).deleteEntry(name);
  }

  public static void deleteFAT(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    FATFileReference mapping = (FATFileReference) fileSystem.getMapping(path);

    int[] fat = fileSystem.getFileAllocationTable();

    int currentIndex = mapping.getFirstBlockIndex();
    int nextIndex;

    while(fat[currentIndex] != FileSystem.UNUSED){
      fileSystem.freeBlock(currentIndex);

      nextIndex = fat[currentIndex];
      fat[currentIndex] = FileSystem.UNUSED;
      currentIndex = nextIndex;
    }

    Directory.findParent(fileSystem, path).deleteEntry(name);
  }

  public static void deleteInode(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    InodeFileReference mapping = (InodeFileReference) fileSystem.getMapping(path);

    Inode inode = Inode.get(fileSystem, mapping.getIndex());
    int[] addresses = inode.allAddresses();

    for(int i = 0; i < addresses.length; i++){
      fileSystem.freeBlock(addresses[i]);
    }

    fileSystem.freeBlock(inode.getSinglyIndirectPointer());

    Directory.findParent(fileSystem, path).deleteEntry(name);

    fileSystem.getFreeInodesBitMap().setFree(mapping.getIndex());
  }

  private static void addToDirectory(
          FileSystem fileSystem,
          String path,
          FileReference mapping
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
