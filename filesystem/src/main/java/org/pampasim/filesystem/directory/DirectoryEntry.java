package org.pampasim.filesystem.directory;

import org.pampasim.filesystem.mapping.FileMapping;
import org.pampasim.filesystem.core.AllocationScheme;
import org.pampasim.filesystem.mapping.ContiguousMapping;
import org.pampasim.filesystem.mapping.FATMapping;

import java.nio.ByteBuffer;

public class DirectoryEntry {

  private String name;
  private FileMapping fileMapping; // the information kept to track the file (and maybe attributes as well)

  public DirectoryEntry(String name, FileMapping fileMapping){
    if(name.toCharArray().length > Directory.FILE_NAME_LENGTH_CHARS){
      throw new IllegalArgumentException("Directory entry with a name too large: " + name);
    }
    this.name = name;
    this.fileMapping = fileMapping;
  }

  public DirectoryEntry(){}

  public FileMapping getFileMapping(){
    return fileMapping;
  }

  public String getName(){
    return name;
  }

  public void setName(String name){
    if(name.toCharArray().length > Directory.FILE_NAME_LENGTH_CHARS){
      throw new IllegalArgumentException("Directory entry with a name too large: " + name);
    }
    this.name = name;
  }

  public int getFileCurrentSizeBytes(AllocationScheme allocationScheme){
    switch(allocationScheme){
      case AllocationScheme.CONTIGUOUS:
        return ((ContiguousMapping) fileMapping).getCurrentSizeBytes();
      case AllocationScheme.FAT:
        return ((FATMapping) fileMapping).getCurrentSizeBytes();
      // Inodes are more complicated since metadata is stored in the inodes, not directory entry
      default:
        throw new Error("unhandled switch case");
    }

  }

  public void writeToBuffer(ByteBuffer buffer){
    char[] nameArray = new char[Directory.FILE_NAME_LENGTH_CHARS];
    char[] tempArray = this.name.toCharArray();
    System.arraycopy(tempArray, 0, nameArray, 0, Math.min(tempArray.length, Directory.FILE_NAME_LENGTH_CHARS));
    for(char c : nameArray){
      buffer.putChar(c);
    }
    fileMapping.writeToBuffer(buffer);
  }

  public int sizeBytes(){
    int bitsInBytes = 8;
    int charSizeBytes = Character.SIZE / bitsInBytes;
    return (Directory.FILE_NAME_LENGTH_CHARS * charSizeBytes) + fileMapping.sizeBytes();
  }

  public boolean isNull(){
    return name.isEmpty();
  }

  public static int sizeBytes(AllocationScheme allocationScheme){
    int bitsInBytes = 8;
    int charSizeBytes = Character.SIZE / bitsInBytes;
    int totalSizeBytes = (Directory.FILE_NAME_LENGTH_CHARS * charSizeBytes);

    totalSizeBytes += FileMapping.sizeBytes(allocationScheme);

    return totalSizeBytes;
  }
  //public static DirectoryEntry getFromDisk(Disk disk, int starting_index, AllocationScheme allocationScheme){
    //String name = ;
  //  FileMapping fileMapping;
  //  switch(allocationScheme){
  //    case allocationScheme.CONTIGUOUS:
        //fileMapping = ContiguousMapping.getFromDisk(disk,);
  //  }
    

  //}
  //
  public static DirectoryEntry getFromBuffer(ByteBuffer buffer, AllocationScheme allocationScheme){
    System.out.println(buffer.capacity());
    char[] nameArray = new char[Directory.FILE_NAME_LENGTH_CHARS];
    for(int i = 0; i < nameArray.length; i++){
      nameArray[i] = buffer.getChar();
    }
    String name = (new String(nameArray)).replace("\0", "");
    FileMapping mapping = FileMapping.getFromBuffer(buffer, allocationScheme);
    /*
    switch(allocationScheme){
      case AllocationScheme.CONTIGUOUS:
        mapping = ContiguousMapping.getFromBuffer(buffer);
      default:
        mapping = ContiguousMapping.getFromBuffer(buffer);
    }
    */

    return new DirectoryEntry(name, mapping);
  }

  @Override
  public String toString(){
    return "DirectoryEntry [name=" + name + ", fileMapping=" + fileMapping + "]";
  }

}
