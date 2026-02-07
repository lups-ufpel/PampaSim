package org.pampasim.filesystem.directory;

import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.file.reference.*;

import java.nio.ByteBuffer;

public class DirectoryEntry {

  private String name;
  private FileReference fileReference; // the information kept to track the file (and maybe attributes as well)

  public DirectoryEntry(String name, FileReference fileReference){
    if(name.toCharArray().length > Directory.FILE_NAME_LENGTH_CHARS){
      throw new IllegalArgumentException("Directory entry with a name too large: " + name);
    }
    this.name = name;
    this.fileReference = fileReference;
  }

  public DirectoryEntry(){}

  public FileReference getFileReference(){
    return fileReference;
  }

  public String getName(){
    return name;
  }

  public boolean isDirectory(FileSystem fileSystem){
      return switch(fileSystem.getAllocationScheme()){
        case CONTIGUOUS -> ((ContiguousFileReference) fileReference).getMetadata().isDirectory();
        case FAT -> ((FATFileReference) fileReference).getMetadata().isDirectory(); 
        case INODES -> ((InodeFileReference) fileReference).getMetadata(fileSystem).isDirectory();
        default -> throw new Error("unhandled switch case");
      };
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
        return ((ContiguousFileReference) fileReference).getCurrentSizeBytes();
      case AllocationScheme.FAT:
        return ((FATFileReference) fileReference).getCurrentSizeBytes();
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
    fileReference.writeToBuffer(buffer);
  }

  public int sizeBytes(){
    int bitsInBytes = 8;
    int charSizeBytes = Character.SIZE / bitsInBytes;
    return (Directory.FILE_NAME_LENGTH_CHARS * charSizeBytes) + fileReference.sizeBytes();
  }

  public boolean isNull(){
    return name.isEmpty();
  }

  public static int sizeBytes(AllocationScheme allocationScheme){
    int bitsInBytes = 8;
    int charSizeBytes = Character.SIZE / bitsInBytes;
    int totalSizeBytes = (Directory.FILE_NAME_LENGTH_CHARS * charSizeBytes);

    totalSizeBytes += FileReference.sizeBytes(allocationScheme);

    return totalSizeBytes;
  }
  //public static DirectoryEntry getFromDisk(Disk disk, int starting_index, AllocationScheme allocationScheme){
    //String name = ;
  //  FileReference fileReference;
  //  switch(allocationScheme){
  //    case allocationScheme.CONTIGUOUS:
        //fileReference = Contiguousreference.getFromDisk(disk,);
  //  }
    

  //}
  //
  public static DirectoryEntry getFromBuffer(ByteBuffer buffer, AllocationScheme allocationScheme){
    char[] nameArray = new char[Directory.FILE_NAME_LENGTH_CHARS];
    for(int i = 0; i < nameArray.length; i++){
      nameArray[i] = buffer.getChar();
    }
    String name = (new String(nameArray)).replace("\0", "");
    FileReference reference = FileReference.getFromBuffer(buffer, allocationScheme);
    /*
    switch(allocationScheme){
      case AllocationScheme.CONTIGUOUS:
        reference = Contiguousreference.getFromBuffer(buffer);
      default:
        reference = Contiguousreference.getFromBuffer(buffer);
    }
    */

    return new DirectoryEntry(name, reference);
  }

  @Override
  public String toString(){
    return "DirectoryEntry [name=" + name + ", fileReference=" + fileReference + "]";
  }

}
