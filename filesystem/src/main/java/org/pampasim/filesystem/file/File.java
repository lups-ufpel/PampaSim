package org.pampasim.filesystem.file;

import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;
import org.pampasim.filesystem.file.reference.*;

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
              new FATFileReference(FATFileReference.FIRST_BLOCK_NOT_SET, metadata);
  
      addToDirectory(fileSystem, path, reference);
  
      return reference;
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

    Directory.findParent(fileSystem, path).deleteEntry(name);
  }

  public static void deleteFAT(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    FATFileReference reference = (FATFileReference) fileSystem.getReference(path);

    int[] fat = fileSystem.getFileAllocationTable();

    int currentIndex = reference.getFirstBlockIndex();
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

    InodeFileReference reference = (InodeFileReference) fileSystem.getReference(path);

    Inode inode = Inode.get(fileSystem, reference.getIndex());
    int[] addresses = inode.allAddresses();

    for(int i = 0; i < addresses.length; i++){
      fileSystem.freeBlock(addresses[i]);
    }

    fileSystem.freeBlock(inode.getSinglyIndirectPointer());

    Directory.findParent(fileSystem, path).deleteEntry(name);

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
      dir.addEntry(new DirectoryEntry(name, reference));
  }

  public static void write(FileSystem fileSystem, String path, byte[] data, int position) {
      FileReference reference =
          Directory.getFileReference(fileSystem, path);
  
      switch (fileSystem.getAllocationScheme()) {
          case CONTIGUOUS ->
              writeToContiguousFile(fileSystem, path, reference, data, position);
  
          case INODES ->
              writeToInodeFile(fileSystem, path, reference, data, position);
  
          case FAT ->
              writeToFATFile(fileSystem, path, reference, data, position);
      }
  }

  private static void writeToContiguousFile(
          FileSystem fileSystem,
          String path,
          FileReference reference,
          byte[] data,
          int position
  ) {
      ContiguousFileReference cref =
          (ContiguousFileReference) reference;
  
      int firstBlockIndex = cref.getFirstBlockIndex();
      int finalSizeBlocks = cref.getFinalSizeBlocks();
  
      if (fileSystem.blocksRequiredFor(
              position + data.length,
              fileSystem.getBlockSizeBytes()
          ) > finalSizeBlocks) {
          throw new Error("write too large");
      }
  
      fileSystem.writeBytes(data, firstBlockIndex, position);
  
      FileMetadata metadata = cref.getMetadata();
      updateMetadataAfterWrite(fileSystem, metadata, path, data.length, position);
  }

  private static void writeToInodeFile(
          FileSystem fileSystem,
          String path,
          FileReference reference,
          byte[] data,
          int position
  ) {
      Inode inode = Inode.get(
          fileSystem,
          ((InodeFileReference) reference).getIndex()
      );
  
      inode.write(fileSystem, data, position);
  
      FileMetadata metadata = inode.getMetadata();
      updateMetadataAfterWrite(fileSystem, metadata, path, data.length, position);
  }

  private static void writeToFATFile(
          FileSystem fileSystem,
          String path,
          FileReference reference,
          byte[] data,
          int position
  ) {

      // needs to update FIRST_BLOCK_NOT_SET
      FATFileReference freference = (FATFileReference) reference;
      int[] fileAllocationTable = fileSystem.getFileAllocationTable();
      
      int blockSizeBytes = fileSystem.getBlockSizeBytes();
      byte[] buffer = data;
      
      int currentIndex = freference.getFirstBlockIndex();
      if (currentIndex == freference.FIRST_BLOCK_NOT_SET) {
          currentIndex = fileSystem.nextFreeBlock();
      }
      
      int currentByte = 0;
      int bufferPointer = 0;
      
      while (bufferPointer < buffer.length) {
      
          int writeOffset = 0;
          int writableBytes = blockSizeBytes;
      
          // First block offset handling
          if (currentByte <= position &&
              position < currentByte + blockSizeBytes) {
      
              writeOffset = position - currentByte;
              writableBytes = blockSizeBytes - writeOffset;
          }
      
          int bytesToWrite = Math.min(
              writableBytes,
              buffer.length - bufferPointer
          );
      
          byte[] chunk = Arrays.copyOfRange(
              buffer,
              bufferPointer,
              bufferPointer + bytesToWrite
          );
      
          fileSystem.writeBytes(chunk, currentIndex, writeOffset);
      
          bufferPointer += bytesToWrite;
          currentByte += blockSizeBytes;
      
          // Advance or extend FAT
          if (fileAllocationTable[currentIndex] == FileSystem.UNUSED) {
              fileAllocationTable[currentIndex] = fileSystem.nextFreeBlock();
          }
      
          currentIndex = fileAllocationTable[currentIndex];
      }
      
      FileMetadata metadata = freference.getMetadata();

      if(data.length + position > metadata.getCurrentSizeBytes()){ 
          metadata.setCurrentSizeBytes(data.length + position);
      } 

      if(data.length > 0){
          metadata.updateLastModified();
      }
      // updates metadata 
      metadata.writeToDisk(fileSystem, path);
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
    return fileSystem.readFromFile(path, byteNumber, position);
  }

}
