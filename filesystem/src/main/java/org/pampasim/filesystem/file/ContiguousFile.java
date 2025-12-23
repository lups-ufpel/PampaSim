package org.pampasim.filesystem.file;

import java.time.Instant;

// abstract because "instances" are on the disk file
abstract public class ContiguousFile extends File{
  //private FileSystem fileSystem;

  //public ContiguousFile(FileSystem fileSystem){
  //  this.fileSystem = fileSystem;
  //}
  //
  /*
  //returns first block index
  public static int create(FileSystem fileSystem, String path, int finalSizeBlocks, boolean isBinary, boolean isDirectory){
    if(finalSizeBlocks > fileSystem.freeBlocksCount()){
      throw new Error("file is too big. File system has " + fileSystem.freeBlocksCount() + " free blocks.");
    }
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    FileMetadata metadata = new FileMetadata(0, isBinary, isDirectory, Instant.now());
    int firstBlockIndex = fileSystem.getFreeBlocksAndSetAllocated(finalSizeBlocks);
    ContiguousMapping mapping = new ContiguousMapping(firstBlockIndex, finalSizeBlocks, metadata);

    Directory dir = Directory.findParent(fileSystem, path);
    // does directory have mapping?
    DirectoryEntry fileEntry = new DirectoryEntry(name, mapping);
    dir.addEntry(fileEntry);

    return firstBlockIndex;
  }


  public static void write(FileSystem fileSystem, String path, int position, byte[] data){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    Directory fileDirectory = Directory.findParent(fileSystem, path);
    DirectoryEntry fileEntry = fileDirectory.findEntry(name);
    int firstFileBlockIndex = ((ContiguousMapping) fileEntry.getFileMapping()).getFirstBlockIndex();
    if(fileSystem.blocksRequiredFor(position + data.length, fileSystem.getBlockSizeBytes()) > ((ContiguousMapping) fileEntry.getFileMapping()).getFinalSizeBlocks()){
      throw new Error("write too large for file " + name);
    }
    fileSystem.writeBytes(data, firstFileBlockIndex, position); // maybe later replace with writeToFile?
  }
  public void read(){}
  */
}
