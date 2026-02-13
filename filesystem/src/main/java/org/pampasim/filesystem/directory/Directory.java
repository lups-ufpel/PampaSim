package org.pampasim.filesystem.directory;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.file.File;
import org.pampasim.filesystem.file.FileMetadata;
import org.pampasim.filesystem.file.reference.*;
import org.pampasim.filesystem.fat.FileAllocationTable;

import java.util.NoSuchElementException;

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
      case CONTIGUOUS -> ((ContiguousFileReference) getDot().getFileReference()).getFirstBlockIndex();
      case FAT -> ((FATFileReference) getDot().getFileReference()).getFirstBlockIndex();
      case INODES -> ((InodeFileReference) getDot().getFileReference()).getIndex();
      default -> throw new Error("unhandled switch case");
    };
  }

  public static FileReference getFileReference(FileSystem fileSystem, String path){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];

    Directory fileDirectory = Directory.findParent(fileSystem, path);
    DirectoryEntry fileEntry = fileDirectory.findEntry(name);

    return fileEntry.getFileReference();
  }

  //hacky
  public void setFATFirstBlockIndex(String path, int firstBlockIndex){
    String[] segments = path.split("/");
    String name = segments[segments.length - 1];
    int entryPosition = getEntryPosition(name);
    int entryFirstByte = entryPosition * DirectoryEntry.sizeBytes(fileSystemHandle.getAllocationScheme());

    DirectoryEntry newreferenceEntry = findEntry(name);
    ((FATFileReference) newreferenceEntry.getFileReference()).setFirstBlockIndex(firstBlockIndex);

    ByteBuffer buffer = ByteBuffer.allocate(DirectoryEntry.sizeBytes(fileSystemHandle.getAllocationScheme()));
    newreferenceEntry.writeToBuffer(buffer);

    File.write(fileSystemHandle, parentPath(path), buffer.array(), entryFirstByte, false);
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
          FileReference reference = entry.getFileReference();
          int entryIndex = switch(fileSystem.getAllocationScheme()){
            case INODES -> ((InodeFileReference) reference).getIndex();
            case CONTIGUOUS -> ((ContiguousFileReference) reference).getFirstBlockIndex();
            case FAT -> ((FATFileReference) reference).getFirstBlockIndex();
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

  public static String parentPath(String path) {
      if (path == null || path.isEmpty()) {
          throw new IllegalArgumentException("Path cannot be null or empty");
      }
  
      if (path.equals("/")) {
          return "/"; // parent of root is root
      }
  
      // Remove trailing slash (except for root)
      if (path.endsWith("/") && path.length() > 1) {
          path = path.substring(0, path.length() - 1);
      }
  
      int lastSlash = path.lastIndexOf('/');
  
      if (lastSlash <= 0) {
          return "/"; // parent of "/a" is "/"
      }
  
      return path.substring(0, lastSlash);
  }

  public DirectoryEntry findEntry(String name){
    for(DirectoryEntry e : entries){
      if(e.getName().equals(name)){
        return e;
      }
    }

    throw new NoSuchElementException("No entry called " + name);
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

    throw new NoSuchElementException("No entry called " + name);
  }

  public static void delete(FileSystem fileSystem, String path){
    Directory dir = Directory.find(fileSystem, path);

    // delete all files within
    for(DirectoryEntry de : dir.getEntries()){
      boolean isDirectory = de.isDirectory(fileSystem);
      String entryPath = path + "/" + de.getName();

      if(de.isNull() || de.getName().equals(".") || de.getName().equals("..")){
        continue;
      }

      if(isDirectory){
        delete(fileSystem, entryPath);

      }  else{
        File.delete(fileSystem, entryPath);

      }

    }

    //delete now-empty dir
    File.delete(fileSystem, path);

  }

  public int getFirstEmptyEntryIndex(){
    AllocationScheme as = fileSystemHandle.getAllocationScheme();
    switch(as){
      case FAT:
      {
        int i = 0;
        for(i = 0; i < entries.size(); i++){
          if(entries.get(i).isNull()){
            return i;
          }
        }
        return i; // i will be 1 more than entries.size() 
      }

      case CONTIGUOUS:
      {

        int i = 0;
        for(i = 0; i < entries.size(); i++){
          if(entries.get(i).isNull()){
            return i;
          }
        }

        throw new Error("Trying to add entry to full directory");
      }

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

  public void writeEntryData(byte[] data, int entryIndex, String dirPath){
    AllocationScheme as = fileSystemHandle.getAllocationScheme();
    int entryFirstByte = entryIndex * DirectoryEntry.sizeBytes(as);

    File.write(fileSystemHandle, dirPath, data, entryFirstByte, true);
  }

  public void addEntry(DirectoryEntry newEntry, String dirPath){
    int i = getFirstEmptyEntryIndex();
    if(i < entries.size()){
      entries.set(i, newEntry);
    }  else{
      entries.add(newEntry);
    }

    ByteBuffer entryBuffer = ByteBuffer.allocate(newEntry.sizeBytes());
    newEntry.writeToBuffer(entryBuffer);

    int entryFirstByte = i * newEntry.sizeBytes();
    writeEntryData(entryBuffer.array(), i, dirPath);
  }

  public void deleteEntry(String name, String dirPath){
    int i = getEntryPosition(name);

    deleteEntry(i, dirPath);
  }

  public void deleteEntry(int index, String dirPath){
    DirectoryEntry blank = new DirectoryEntry();
    if(index < entries.size()){
      entries.set(index, blank);
    }

    AllocationScheme as = fileSystemHandle.getAllocationScheme();
    int sizeBytes = DirectoryEntry.sizeBytes(as);
    int entryFirstByte = index * DirectoryEntry.sizeBytes(as);

    byte[] blankArray = new byte[sizeBytes];

    // clears entry data
    writeEntryData(blankArray, index, dirPath);

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
  
      FileReference dotreference =
              File.createContiguous(
                      fileSystem,
                      path,
                      currentSizeBytes,
                      finalSizeBlocks,
                      true
              );
  
      writeInitialDirectoryContents(fileSystem, path, dotreference, currentSizeBytes);
  }

  public static void createInodes(
          FileSystem fileSystem,
          String path
  ) {
      int requiredEntries = 2;
      int currentSizeBytes =
              DirectoryEntry.sizeBytes(fileSystem.getAllocationScheme()) * requiredEntries;
  
      FileReference dotreference =
              File.createInodes(
                      fileSystem,
                      path,
                      currentSizeBytes,
                      true
              );
  
      writeInitialDirectoryContents(fileSystem, path, dotreference, currentSizeBytes);
  }

  public static void createFAT(
          FileSystem fileSystem,
          String path
  ) {
      int requiredEntries = 2;
      int currentSizeBytes =
              DirectoryEntry.sizeBytes(fileSystem.getAllocationScheme()) * requiredEntries;
  
      FileReference dotreference =
              File.createFAT(
                      fileSystem,
                      path,
                      currentSizeBytes,
                      true
              );
  
      writeInitialDirectoryContents(fileSystem, path, dotreference, currentSizeBytes);
  }

  private static void writeInitialDirectoryContents(
          FileSystem fileSystem,
          String path,
          FileReference dotreference,
          int currentSizeBytes
  ) {
      ArrayList<DirectoryEntry> entries = new ArrayList<>();
  
      DirectoryEntry dot = new DirectoryEntry(".", dotreference);
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
  
      File.write(fileSystem, path, buffer.array(), 0, true);
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
        while(dataPosition < dotData.length && nextBlock != FileAllocationTable.EOF){
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
        int sizeBlocks = ((ContiguousFileReference) dot.getFileReference()).getFinalSizeBlocks();
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
      
          int currentSizeBytes =
                  ((FATFileReference) dot.getFileReference()).getCurrentSizeBytes();
      
          int blockSize = fileSystem.getBlockSizeBytes();
      
          directoryData = new byte[currentSizeBytes];
      
          int nextBlock = firstBlockIndex;
          int dataPosition = 0;
      
          while (nextBlock != FileAllocationTable.EOF && dataPosition < currentSizeBytes) {
      
              byte[] block = fileSystem.readBlock(nextBlock);
      
              int bytesToCopy = Math.min(
                      blockSize,
                      currentSizeBytes - dataPosition
              );
      
              System.arraycopy(
                      block,
                      0,
                      directoryData,
                      dataPosition,
                      bytesToCopy
              );
      
              dataPosition += bytesToCopy;
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

    while(buffer.remaining() >= DirectoryEntry.sizeBytes(allocationScheme)){
      entries.add(DirectoryEntry.getFromBuffer(buffer, allocationScheme));
    }

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
