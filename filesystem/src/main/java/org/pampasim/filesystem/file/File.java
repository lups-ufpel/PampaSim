package org.pampasim.filesystem.file;

import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;
import org.pampasim.filesystem.file.reference.*;
import org.pampasim.filesystem.fat.FileAllocationTable;

import java.time.Instant;
import java.util.Arrays;

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
  
      FileReference reference =
              new ContiguousFileReference(firstBlockIndex, finalSizeBlocks, metadata);
  
      BlockType type = (isDirectory) ? BlockType.DIRECTORY : BlockType.FILE;
      fileSystem.setBlockRecord(firstBlockIndex, firstBlockIndex + finalSizeBlocks, type, path);
      addToDirectory(fileSystem, path, reference);
  
      return reference;
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
  
      FileReference reference =
              new InodeFileReference(fileSystem.nextFreeInode());
  
      addToDirectory(fileSystem, path, reference);
  
      // must happen after directory entry exists
      metadata.writeToDisk(fileSystem, path);
  
      return reference;
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
  
      FileReference reference =
              new FATFileReference(FileAllocationTable.EOF, metadata);
  
      addToDirectory(fileSystem, path, reference);
  
      return reference;
  }

  public static void setFileBlockRecords(
        FileSystem fileSystem,
        int currentSizeBytes,
        boolean isDirectory
  ) {
    if(currentSizeBytes == 0){
      return;
    }

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

    ContiguousFileReference reference = (ContiguousFileReference) fileSystem.getReference(path);
    int firstBlockIndex = reference.getFirstBlockIndex();
    int lastBlockIndex = firstBlockIndex + reference.getFinalSizeBlocks();

    fileSystem.freeBlocks(firstBlockIndex, lastBlockIndex);

    Directory.findParent(fileSystem, path).deleteEntry(name, Directory.parentPath(path));
  }

  public static void deleteFAT(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    FATFileReference reference = (FATFileReference) fileSystem.getReference(path);

    int[] fat = fileSystem.getFileAllocationTable();

    int currentIndex = reference.getFirstBlockIndex();
    int nextIndex;

    while(fat[currentIndex] != FileAllocationTable.EOF){
      fileSystem.freeBlock(currentIndex);

      nextIndex = fat[currentIndex];
      fat[currentIndex] = FileAllocationTable.UNUSED;
      currentIndex = nextIndex;
    }

    fat[currentIndex] = FileAllocationTable.UNUSED;

    Directory.findParent(fileSystem, path).deleteEntry(name, Directory.parentPath(path));
  }

  public static void deleteInode(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    InodeFileReference reference = (InodeFileReference) fileSystem.getReference(path);

    Inode inode = Inode.get(fileSystem, reference.getIndex());
    int[] addresses = inode.allAddresses();

    for(int i = 0; i < addresses.length; i++){
      fileSystem.freeBlock(addresses[i]);
    }

    fileSystem.freeBlock(inode.getSinglyIndirectPointer());

    Directory.findParent(fileSystem, path).deleteEntry(name, Directory.parentPath(path));

    fileSystem.getFreeInodesBitMap().setFree(reference.getIndex());
  }

  private static void addToDirectory(
          FileSystem fileSystem,
          String path,
          FileReference reference
  ) {
      String[] segments = path.split("/");
      String name = segments[segments.length - 1];
      Directory dir = Directory.findParent(fileSystem, path);
      dir.addEntry(new DirectoryEntry(name, reference), Directory.parentPath(path));
  }

  public static void write(FileSystem fileSystem, String path, byte[] data, int position, boolean updateMetadata) {
      FileReference reference =
          fileSystem.getReference(path);
  
      switch (fileSystem.getAllocationScheme()) {
          case CONTIGUOUS ->
              writeToContiguousFile(fileSystem, path, (ContiguousFileReference) reference, data, position, updateMetadata);
  
          case INODES ->
              writeToInodeFile(fileSystem, path, (InodeFileReference) reference, data, position, updateMetadata);
  
          case FAT ->
              writeToFATFile(fileSystem, path, (FATFileReference) reference, data, position, updateMetadata);

          default -> throw new Error("unhandled switch case");
      }
  }

  private static void writeToContiguousFile(
          FileSystem fileSystem,
          String path,
          ContiguousFileReference reference,
          byte[] data,
          int position,
          boolean updateMetadata
  ) {
  
      int firstBlockIndex = reference.getFirstBlockIndex();
      int finalSizeBlocks = reference.getFinalSizeBlocks();
  
      if (fileSystem.blocksRequiredFor(
              position + data.length,
              fileSystem.getBlockSizeBytes()
          ) > finalSizeBlocks) {
          throw new Error("write too large");
      }
  
      fileSystem.writeBytes(data, firstBlockIndex, position);
  
      FileMetadata metadata = reference.getMetadata();
      if(updateMetadata){
        updateMetadataAfterWrite(fileSystem, metadata, path, data.length, position);
      }
  }

  private static void writeToInodeFile(
          FileSystem fileSystem,
          String path,
          InodeFileReference reference,
          byte[] data,
          int position,
          boolean updateMetadata
  ) {
      Inode inode = Inode.get(
          fileSystem,
          reference.getIndex()
      );
  
      inode.write(fileSystem, data, position);
  
      FileMetadata metadata = inode.getMetadata();
      if(updateMetadata){
        updateMetadataAfterWrite(fileSystem, metadata, path, data.length, position);
      }
  }

  private static void writeToFATFile(
          FileSystem fileSystem,
          String path,
          FATFileReference reference,
          byte[] data,
          int position,
          boolean updateMetadata
  ) {

      int firstBlock = ((FATFileReference) reference).getFirstBlockIndex();
      FileAllocationTable.writeIntoFAT(fileSystem, data, firstBlock, position);
      
      FileMetadata metadata = reference.getMetadata();
      if(updateMetadata){
        updateMetadataAfterWrite(fileSystem, metadata, path, data.length, position);
      }
  }

  private static void updateMetadataAfterWrite(
          FileSystem fileSystem,
          FileMetadata metadata,
          String path,
          int length,
          int position
  ) {
      int newSize = position + length;
      if (newSize > metadata.getCurrentSizeBytes()) {
          metadata.setCurrentSizeBytes(newSize);
      }
  
      if (length > 0) {
          metadata.updateLastModified();
      }
  
      metadata.writeToDisk(fileSystem, path);
  }

  public static byte[] read(FileSystem fileSystem, String path, int byteNumber, int position){

    FileReference reference = fileSystem.getReference(path);
  
    return switch(fileSystem.getAllocationScheme()){
      case CONTIGUOUS -> readContiguous(fileSystem, (ContiguousFileReference) reference, byteNumber, position, path);
      case FAT -> readFAT(fileSystem, (FATFileReference) reference, byteNumber, position, path);
      case INODES -> readInodes(fileSystem, (InodeFileReference) reference, byteNumber, position);
      default -> throw new Error("unhandled switch case");
    };
  }

  public static byte[] readContiguous(FileSystem fileSystem, ContiguousFileReference reference, int byteNumber, int position, String path){
    int firstBlockIndex = reference.getFirstBlockIndex();
    int finalSizeBlocks = reference.getFinalSizeBlocks();
    if(fileSystem.blocksRequiredFor(position + byteNumber, fileSystem.getBlockSizeBytes()) > finalSizeBlocks){
      throw new Error("read too large");
    }
    byte[] data = fileSystem.readBytes(byteNumber, firstBlockIndex, position);
    FileMetadata metadata = reference.getMetadata();
    metadata.updateLastAccess();

    // updates metadata, should be write To disk method on metadata?
    metadata.writeToDisk(fileSystem, path);
    return data;
  }

  public static byte[] readFAT(FileSystem fileSystem, FATFileReference reference, int byteNumber, int position, String path){
    int firstBlockIndex = reference.getFirstBlockIndex();

    int nextBlock = firstBlockIndex;
    byte[] data = new byte[byteNumber];
    int dataPosition = 0;
    int copyAmount = fileSystem.getBlockSizeBytes();
    while(nextBlock != FileAllocationTable.UNUSED){

      boolean willCopyTooMuch = dataPosition + copyAmount > byteNumber;
      if(willCopyTooMuch){
        int missingUntilByteNumber = byteNumber - dataPosition;
        copyAmount = missingUntilByteNumber;
      }

      System.arraycopy(fileSystem.readBlock(nextBlock), 0, data, dataPosition, fileSystem.getBlockSizeBytes());
      position += fileSystem.getBlockSizeBytes();
      nextBlock = fileSystem.getFileAllocationTable()[nextBlock];
    }

    FileMetadata metadata = reference.getMetadata();
    metadata.updateLastAccess();
    metadata.writeToDisk(fileSystem, path);

    return data;
  }

  public static byte[] readInodes(FileSystem fileSystem, InodeFileReference reference, int byteNumber, int position){
    Inode fileInode = Inode.get(fileSystem, reference.getIndex());
    byte[] data = fileInode.read(fileSystem, byteNumber, position);
    FileMetadata metadata = fileInode.getMetadata();
    
    metadata.updateLastAccess();

    return data;
  }

}
