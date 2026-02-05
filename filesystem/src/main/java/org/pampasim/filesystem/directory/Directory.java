package org.pampasim.filesystem.directory;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.AllocationScheme;
import org.pampasim.filesystem.mapping.*;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.file.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.time.Instant;

// probably "heap" directories are best, storing only pointer to name + attributes (or maybe this is too low level?)
// make abstract?
public class Directory{
  
  ArrayList<DirectoryEntry> entries = new ArrayList<>();
  private FileSystem fileSystemHandle;
  public static final int FILE_NAME_LENGTH_CHARS = 32;
  private static final int DOT_ENTRY_INDEX = 0;

  /*
  public Directory(DirectoryEntry dot, DirectoryEntry dotdot){
    entries.add(dot);
    entries.add(dotdot);
  }
  */

  public Directory(ArrayList<DirectoryEntry> entries, FileSystem fileSystemHandle){
    this.entries = entries;
    this.fileSystemHandle = fileSystemHandle;
  }

  public Directory(){}

  public ArrayList<DirectoryEntry> getEntries(){
    return entries;
  }

  public DirectoryEntry getDot(){
    return entries.get(DOT_ENTRY_INDEX);
  }

  public int getIndex(){
    return switch(fileSystemHandle.getAllocationScheme()){
      case CONTIGUOUS -> ((ContiguousMapping) getDot().getFileMapping()).getFirstBlockIndex();
      case FAT -> ((FATMapping) getDot().getFileMapping()).getFirstBlockIndex();
      case INODES -> ((InodeMapping) getDot().getFileMapping()).getIndex();
      default -> throw new Error("unhandled switch case");
    };
  }

  public static FileMapping getFileMapping(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    Directory fileDirectory = Directory.findParent(fileSystem, path);
    DirectoryEntry fileEntry = fileDirectory.findEntry(name);

    return fileEntry.getFileMapping();
  }

  //hacky
  public void setFATFirstBlockIndex(String path, int firstBlockIndex){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];
    int entryPosition = getEntryPosition(name);
    int entryFirstByte = entryPosition * DirectoryEntry.sizeBytes(fileSystemHandle.getAllocationScheme());

    DirectoryEntry newMappingEntry = findEntry(name);
    ((FATMapping) newMappingEntry.getFileMapping()).setFirstBlockIndex(firstBlockIndex);

    ByteBuffer buffer = ByteBuffer.allocate(DirectoryEntry.sizeBytes(fileSystemHandle.getAllocationScheme()));
    newMappingEntry.writeToBuffer(buffer);

    fileSystemHandle.writeToFile(parentPath(path), buffer.array(), entryFirstByte);
  }

  public static Directory find(FileSystem fileSystem, String path){
    String[] segments = path.split("/");

    int rootDirectoryIndex = fileSystem.getRootDirectoryIndex();
    Directory currentDirectory = getFromDisk(rootDirectoryIndex, fileSystem);

    int skipRootSegment = 1;
    for(int i = skipRootSegment; i < segments.length; i++){
      String s = segments[i];

      boolean foundSegment = false;
      for(DirectoryEntry entry : currentDirectory.getEntries()){
        if(entry.getName().equals(s)){
          FileMapping mapping = entry.getFileMapping();
          int entryIndex = switch(fileSystem.getAllocationScheme()){
            case INODES -> ((InodeMapping) mapping).getIndex();
            case CONTIGUOUS -> ((ContiguousMapping) mapping).getFirstBlockIndex();
            case FAT -> ((FATMapping) mapping).getFirstBlockIndex();
            default -> throw new Error("Unhandled switch case");
          };
          currentDirectory = getFromDisk(entryIndex, fileSystem);
          foundSegment = true;
          break;
        }
      }

      if(!foundSegment){
        throw new Error("Invalid path: " + path);
      }
    }

    return currentDirectory;

  }

// not tested beyong root dir
  public static Directory findParent(FileSystem fileSystem, String path){
    return Directory.find(fileSystem, parentPath(path));
  }

  public static String parentPath(String path){
    if(path.equals("/")){
      return path; // parent of root is root
    }

    String[] segments = path.split("/");
    String newPath = "";
    //removes file name
    for(int i = 0; i < segments.length - 1; i++)
    {
      newPath += segments[i] + "/";
    }

    return newPath.substring(0, newPath.length() - 1);
  }

  public DirectoryEntry findEntry(String name){
    for(DirectoryEntry e : entries){
      if(e.getName().equals(name)){
        return e;
      }
    }

    throw new Error("no entry called " + name);
  }



  public int getEntryPosition(String name){
    int i = 0;
    for(DirectoryEntry e : entries){
      if(e.getName().equals(name)){
        return i;
      }  else {
        i++;
      }
    }

    throw new Error("no entry called " + name);
  }

  public int getFirstEmptyEntryIndex(){
    AllocationScheme as = fileSystemHandle.getAllocationScheme();
    switch(as){
      case FAT: // fall-through
      case CONTIGUOUS:

        for(int i = 0; i < entries.size(); i++){
          if(entries.get(i).isNull()){
            return i;
          }
        }

        throw new Error("Trying to add entry to full directroy");

      case INODES:
        // for ... 
        int position = 0;
        Inode dirInode = Inode.get(fileSystemHandle, getIndex());

        //TODO: assumes inode is not full
        int i = 0;
        while(true){

          int entrySize = DirectoryEntry.sizeBytes(as);
          ByteBuffer entryBuffer = ByteBuffer.wrap(dirInode.read(fileSystemHandle, entrySize, position));
          if(entryBuffer.limit() < entrySize || DirectoryEntry.getFromBuffer(entryBuffer, as).isNull()){
            return i;
          }

          position += DirectoryEntry.sizeBytes(as);
          i++;
        }

  }
    throw new Error("unreachable");

  }

  public void writeEntryData(byte[] data, int entryIndex){
    int i = entryIndex;
    AllocationScheme as = fileSystemHandle.getAllocationScheme();
    int entryFirstByte = i * DirectoryEntry.sizeBytes(as);

    switch(as){
      case CONTIGUOUS:
      {

        int directoryFirstIndex = getIndex();
        // maybe write too file is too different from writing to dir to use that
        fileSystemHandle.writeBytes(data, directoryFirstIndex, entryFirstByte);

        return;
      }

      case INODES:

        // TODO: not updating metadata (maybe it is inside write actually)
        int inodeIndex = getIndex();
        Inode current = Inode.get(fileSystemHandle, inodeIndex);
        current.write(fileSystemHandle, data, entryFirstByte);

        return;

      case FAT:
        int directoryFirstIndex = getIndex();
        int[] fat = fileSystemHandle.getFileAllocationTable();
        
        int blockSizeBytes = fileSystemHandle.getBlockSizeBytes();
        byte[] buffer = data;
        
        int currentIndex = directoryFirstIndex;
        int currentByte = 0;
        int bufferPointer = 0;
        
        while (bufferPointer < buffer.length) {
        
            int writeOffset = 0;
            int writableBytes = blockSizeBytes;
        
            // First block offset handling
            if (currentByte <= entryFirstByte &&
                entryFirstByte < currentByte + blockSizeBytes) {
        
                writeOffset = entryFirstByte - currentByte;
                writableBytes = blockSizeBytes - writeOffset;
            }
        
            int bytesToWrite = Math.min(writableBytes, buffer.length - bufferPointer);
        
            byte[] chunk = Arrays.copyOfRange(
                buffer,
                bufferPointer,
                bufferPointer + bytesToWrite
            );
        
            fileSystemHandle.writeBytes(chunk, currentIndex, writeOffset);
        
            bufferPointer += bytesToWrite;
            currentByte += blockSizeBytes;
        
            // Advance or extend FAT
            if (fat[currentIndex] == FileSystem.UNUSED) {
                fat[currentIndex] = fileSystemHandle.nextFreeBlock();
            }
        
            currentIndex = fat[currentIndex];
        }



      default:
        throw new Error("unhandled switch case: " + fileSystemHandle.getAllocationScheme());
    }


  }

// only for contiguous for now, missing current size bytes increase
  public void addEntry(DirectoryEntry newEntry){
    int i = getFirstEmptyEntryIndex();
    if(i < entries.size()){
      entries.set(i, newEntry);
    }  else{
      entries.add(newEntry);
    }

    ByteBuffer entryBuffer = ByteBuffer.allocate(newEntry.sizeBytes());
    newEntry.writeToBuffer(entryBuffer);

    writeEntryData(entryBuffer.array(), i);
  }

  public void deleteEntry(String name){
    DirectoryEntry blank = new DirectoryEntry();

    int i = getEntryPosition(name);
    if(i < entries.size()){
      entries.set(i, blank);
    }

    AllocationScheme as = fileSystemHandle.getAllocationScheme();
    int sizeBytes = DirectoryEntry.sizeBytes(as);
    int entryFirstByte = i * DirectoryEntry.sizeBytes(as);

    byte[] blankArray = new byte[sizeBytes];

    // clears entry data
    writeEntryData(blankArray, i);

  }

  public static void createContiguous(
          FileSystem fileSystem,
          String path,
          int finalSizeBlocks
  ) {
      int requiredEntries = 2;
      int currentSizeBytes =
              DirectoryEntry.sizeBytes(fileSystem.getAllocationScheme()) * requiredEntries;
  
      if (finalSizeBlocks < fileSystem.blocksRequiredFor(currentSizeBytes)) {
          throw new Error("Directory " + path + " too small for essential entries.");
      }
  
      FileMapping dotMapping =
              File.createContiguous(
                      fileSystem,
                      path,
                      finalSizeBlocks,
                      true
              );
      ((ContiguousMapping) dotMapping).setCurrentSizeBytes(currentSizeBytes);
  
      writeInitialDirectoryContents(fileSystem, path, dotMapping, currentSizeBytes);
  }

  public static void createInodes(
          FileSystem fileSystem,
          String path
  ) {
      int requiredEntries = 2;
      int currentSizeBytes =
              DirectoryEntry.sizeBytes(fileSystem.getAllocationScheme()) * requiredEntries;
  
      FileMapping dotMapping =
              File.createInodes(
                      fileSystem,
                      path,
                      true
              );
      ((InodeMapping) dotMapping).setCurrentSizeBytes(fileSystem, currentSizeBytes);
  
      writeInitialDirectoryContents(fileSystem, path, dotMapping, currentSizeBytes);
  }

  public static void createFAT(
          FileSystem fileSystem,
          String path
  ) {
      int requiredEntries = 2;
      int currentSizeBytes =
              DirectoryEntry.sizeBytes(fileSystem.getAllocationScheme()) * requiredEntries;
  
      FileMapping dotMapping =
              File.createFAT(
                      fileSystem,
                      path,
                      true
              );
      ((FATMapping) dotMapping).setCurrentSizeBytes(currentSizeBytes);
  
      writeInitialDirectoryContents(fileSystem, path, dotMapping, currentSizeBytes);
  }

  private static void writeInitialDirectoryContents(
          FileSystem fileSystem,
          String path,
          FileMapping dotMapping,
          int currentSizeBytes
  ) {
      ArrayList<DirectoryEntry> entries = new ArrayList<>();
  
      DirectoryEntry dot = new DirectoryEntry(".", dotMapping);
      entries.add(dot);
  
      Directory parent = findParent(fileSystem, path);
      DirectoryEntry dotdotEntry = parent.getEntries().get(DOT_ENTRY_INDEX);
      dotdotEntry.setName("..");
      entries.add(dotdotEntry);
  
      ByteBuffer buffer = ByteBuffer.allocate(currentSizeBytes);
      Directory dir = new Directory(entries, fileSystem);
      Directory.writeToBuffer(dir, buffer);
  
      while (buffer.position() < buffer.limit()) {
          buffer.put((byte) 0);
      }
  
      File.write(fileSystem, path, buffer.array(), 0);
  }

  // maybe should interact with file system instead of disk?? we can be sure starts at block start
  /*
  public static Directory getFromDisk(Disk disk, int relative_starting_index, AllocationScheme allocationScheme){

    int directoryEntrySizeBytes = DirectoryEntry.sizeBytes(allocationScheme);
    ByteBuffer dotEntryBuffer = ByteBuffer.allocate(directoryEntrySizeBytes);
    byte[][] dotEntryBlocks = FileSystem.getBytesAsBlocks(directoryEntrySizeBytes, relative_starting_index, disk);

    for(int i = 0; i < directoryEntrySizeBytes; i++){
      dotEntryBuffer.put(dotEntryBlocks[i / disk.getBlockSizeBytes()][i % disk.getBlockSizeBytes()]);
    }

    DirectoryEntry dot = DirectoryEntry.getFromBuffer(dotEntryBuffer, allocationScheme);

    //temp
    return new Directory(dot, dot, allocationScheme);
  }
  */

  public static int getCurrentSizeBytes(int relative_starting_index, FileSystem fileSystem){
        AllocationScheme as = fileSystem.getAllocationScheme();
        byte[] dotEntryBytes = fileSystem.readBytes(DirectoryEntry.sizeBytes(as), relative_starting_index, 0);

        return DirectoryEntry.getFromBuffer(ByteBuffer.wrap(dotEntryBytes), as).getFileCurrentSizeBytes(as);
  }

  public static DirectoryEntry getDotFromDisk(int relative_starting_index, FileSystem fileSystem){
    AllocationScheme as = fileSystem.getAllocationScheme();
    return switch(as){
      case CONTIGUOUS:

        byte[] dotEntryBytes = fileSystem.readBytes(DirectoryEntry.sizeBytes(as), relative_starting_index, 0);
        yield DirectoryEntry.getFromBuffer(ByteBuffer.wrap(dotEntryBytes), as);

      case FAT:
        
        //rounded up to fit blocks
        byte[] dotData = new byte[fileSystem.blocksRequiredFor(DirectoryEntry.sizeBytes(as)) * fileSystem.getBlockSizeBytes()];
        int nextBlock = relative_starting_index;
        int dataPosition = 0;
        while(dataPosition < dotData.length){

          System.arraycopy(fileSystem.readBlock(nextBlock), 0, dotData, dataPosition, fileSystem.getBlockSizeBytes());
          dataPosition += fileSystem.getBlockSizeBytes();
          nextBlock = fileSystem.getFileAllocationTable()[nextBlock];
        }

        yield DirectoryEntry.getFromBuffer(ByteBuffer.wrap(dotData), as);

      default:
        throw new Error("Not implemented");
    };
  }

  public static Directory getFromDisk(int fileIndex, FileSystem fileSystem){
    byte[] directoryData;
    switch(fileSystem.getAllocationScheme()){
      case CONTIGUOUS:
        {
        int relative_starting_index = fileIndex;
        

        DirectoryEntry dot = getDotFromDisk(relative_starting_index, fileSystem);
        int sizeBlocks = ((ContiguousMapping) dot.getFileMapping()).getFinalSizeBlocks();
        System.out.println("finalSize:" + sizeBlocks);
        byte[][] directoryBlocks = fileSystem.readBlocks(relative_starting_index, sizeBlocks);
        directoryData = FileSystem.flatten(directoryBlocks);
        break;
        }

      case INODES:
        int inode_index = fileIndex;
        // need to update current size bytes properly
        directoryData = Inode.read(fileSystem, inode_index, Inode.getMetadata(fileSystem, inode_index).getCurrentSizeBytes(), 0);
        break;

      case FAT:
        {
        int firstBlockIndex = fileIndex;

        DirectoryEntry dot = getDotFromDisk(fileIndex, fileSystem);
        // current size bytes rounded up to be divisible by blocks
        int size = fileSystem.blocksRequiredFor(((FATMapping) dot.getFileMapping()).getCurrentSizeBytes()) * fileSystem.getBlockSizeBytes();
        directoryData = new byte[size];
        int nextBlock = firstBlockIndex;
        int dataPosition = 0;
        int copyAmount = fileSystem.getBlockSizeBytes();
        while(nextBlock != FileSystem.UNUSED){

          System.arraycopy(fileSystem.readBlock(nextBlock), 0, directoryData, dataPosition, fileSystem.getBlockSizeBytes());
          dataPosition += fileSystem.getBlockSizeBytes();
          nextBlock = fileSystem.getFileAllocationTable()[nextBlock];
        }

        break;
        }

      default:
        throw new Error("unhandled switch case");
    }

    return getFromBuffer(ByteBuffer.wrap(directoryData), fileSystem);

  }

  public static Directory getFromBuffer(ByteBuffer buffer, FileSystem fileSystem){
    AllocationScheme allocationScheme = fileSystem.getAllocationScheme();
    ArrayList<DirectoryEntry> entries = new ArrayList<>();
    DirectoryEntry dot = DirectoryEntry.getFromBuffer(buffer, allocationScheme);
    entries.add(dot);

    while(buffer.position() + DirectoryEntry.sizeBytes(allocationScheme) <= buffer.limit()){
      entries.add(DirectoryEntry.getFromBuffer(buffer, allocationScheme));
      //System.out.println("here, " + buffer.position());
    }
    //System.out.println("side A: " + (buffer.position() + DirectoryEntry.sizeBytes(allocationScheme)));
    //System.out.println("side B: " + buffer.limit());
    //System.out.println("result: " + (buffer.position() + DirectoryEntry.sizeBytes(allocationScheme) < buffer.limit()));


    return new Directory(entries, fileSystem);
  }

  public static void writeToBuffer(Directory directory, ByteBuffer buffer){
    for(DirectoryEntry e : directory.getEntries()){
      e.writeToBuffer(buffer);
    }
  }


  @Override
  public String toString(){
    String directoryString = "Directory[";
    for(DirectoryEntry e : entries){
      directoryString += "\n";
      directoryString += e.toString();
    }
    directoryString += "]\n";
    return directoryString;
  }
}
