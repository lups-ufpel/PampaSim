package org.pampasim.filesystem.file;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.core.AllocationScheme;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;
import org.pampasim.filesystem.inode.Inode;

import java.time.Instant;
import java.nio.ByteBuffer;
import java.lang.reflect.Field;
import java.util.Vector;
import java.time.temporal.ChronoUnit;

public class FileMetadata{
  
  private int currentSizeBytes;
  private Instant creationTime; // converts to epoch milli internally
  private Instant lastAccess;
  private Instant lastModified;
  private boolean isBinary; // other option is ASCII
  private boolean isDirectory;
  //private boolean isReadOnly; // maybe too scope too big 
  //private boolean isSystem; // maybe too scope too big 
  
  public FileMetadata(int currentSizeBytes, boolean isBinary, boolean isDirectory, Instant creationTime){
    this.currentSizeBytes = currentSizeBytes;
    this.isBinary = isBinary;
    this.isDirectory = isDirectory;
    this.creationTime = creationTime;
    this.lastAccess = creationTime;
    this.lastModified = creationTime;
  }


  public FileMetadata(int currentSizeBytes, boolean isBinary, boolean isDirectory, Instant creationTime, 
    Instant lastAccess, Instant lastModiefied){
    this.currentSizeBytes = currentSizeBytes;
    this.isBinary = isBinary;
    this.isDirectory = isDirectory;
    this.creationTime = creationTime;
    this.lastAccess = creationTime;
    this.lastModified = creationTime;
  }

  public void updateLastAccess(){
    lastAccess = Instant.now();
  }

  public void updateLastModified(){
    lastModified = Instant.now();
  }

  public static int sizeBytes(){
    Class<FileMetadata> FileMetadata = FileMetadata.class;
    Field[] fields = FileMetadata.getDeclaredFields();

    int ints = 0;
    int longs = 0;
    int bools = 0;
    for(Field f : fields){
      Class t = f.getType();

      if(t == int.class){
        ints++;
      }  else if(t == long.class || t == Instant.class){ // Instants are stored as longs
        longs++;
      }  else if(t == boolean.class){
        bools++;
      }  else {
        throw new Error("TotalSizeBytes method is out of date with FileMetadata attributes");
      }

    }

    int totalSizeBytes = 0;

    int bitsInBytes = 8;
    int intSizeBytes = Integer.SIZE / bitsInBytes;
    totalSizeBytes += ints * intSizeBytes;

    int longSizeBytes = Long.SIZE / bitsInBytes;
    totalSizeBytes += longs * longSizeBytes;

    int booleanSizeBytes = 1;
    totalSizeBytes += bools * booleanSizeBytes;

    return totalSizeBytes;
  }

  public void writeToDisk(FileSystem fileSystem, String path){


    int firstBlock;
    int offset;
    switch(fileSystem.getAllocationScheme()){
      case AllocationScheme.INODES:
        int index = fileSystem.getFileIndex(path);
        int inodePosition = Inode.sizeBytes() * index; // metadata is at beggining

        firstBlock = fileSystem.INODES_INDEX; 
        offset = inodePosition;
        break;

      case AllocationScheme.CONTIGUOUS:
        String[] segments = path.split("/");
        String name = segments[segments.length - 1];

        Directory fileDirectory = Directory.findParent(fileSystem, path);
        int entryPosition = fileDirectory.getEntryPosition(name);
        int entryBytePosition = (DirectoryEntry.sizeBytes(AllocationScheme.CONTIGUOUS) * entryPosition);
        int metadataBytePosition = entryBytePosition + Directory.FILE_NAME_LENGTH_CHARS * (Character.SIZE / 8);
        int firstBlockIndex = fileSystem.getFileIndex(path);
        
        firstBlock = firstBlockIndex;
        offset = metadataBytePosition;

        break;
      default:
        throw new Error("unhandled switch case");
    }

    ByteBuffer buffer = ByteBuffer.allocate(this.sizeBytes());
    this.writeToBuffer(buffer);

    //TODO: no checks? maybe does not need it because metadata will always fit
    fileSystem.writeBytes(buffer.array(), firstBlock, offset);
  }

/*
  public void writeToDisk(FileSystem fileSystem, int index){
    ByteBuffer buffer = ByteBuffer.allocate(sizeBytes());

    buffer.putInt(currentSizeBytes);

    long creationTimeEpochMilli = creationTime.toEpochMilli();
    buffer.putLong(creationTimeEpochMilli);

    long lastAccessEpochMilli = lastAccess.toEpochMilli();
    buffer.putLong(lastAccessEpochMilli);

    long lastModifiedEpochMilli = lastModified.toEpochMilli();
    buffer.putLong(lastModifiedEpochMilli);

    buffer.put((byte) (isBinary ? 1 : 0));

    buffer.put((byte) (isDirectory ? 1 : 0));

    switch(fileSystem.getAllocationScheme()){
      case AllocationScheme.CONTIGUOUS:
        fileSystem.writeBytes(buffer.array(), starting_index, 0);

      break;

      case AllocationScheme.INODES:

      break;

      default:
        throw new Error("Unhandled switch case");
    }

  }
  */
  public void writeToBuffer(ByteBuffer buffer){
    buffer.putInt(currentSizeBytes);

    long creationTimeEpochMilli = creationTime.toEpochMilli();
    buffer.putLong(creationTimeEpochMilli);

    long lastAccessEpochMilli = lastAccess.toEpochMilli();
    buffer.putLong(lastAccessEpochMilli);

    long lastModifiedEpochMilli = lastModified.toEpochMilli();
    buffer.putLong(lastModifiedEpochMilli);

    buffer.put((byte) (isBinary ? 1 : 0));

    buffer.put((byte) (isDirectory ? 1 : 0));
  }

  // reads the disk and returns a legible form
  // only works if occupies a whole block for now
  public static FileMetadata getFromDisk(Disk disk, int starting_index){
    ByteBuffer buffer = ByteBuffer.allocate(sizeBytes());

    byte[] block = disk.readBlock(starting_index);
    for(int i = 0; i < sizeBytes(); i++){
      if(i % disk.getBlockSizeBytes() == 0){
        block = disk.readBlock(starting_index + (i / disk.getBlockSizeBytes()));
      }
      buffer.put(block[i % disk.getBlockSizeBytes()]);
    }
    
    buffer.rewind();
    return getFromBuffer(buffer);
  }

  public static FileMetadata getFromBuffer(ByteBuffer buffer){
    int currentSizeBytes = buffer.getInt();
    Instant creationTime = Instant.ofEpochMilli(buffer.getLong());
    Instant lastAccessTime = Instant.ofEpochMilli(buffer.getLong());
    Instant lastModifiedTime = Instant.ofEpochMilli(buffer.getLong());
    boolean isBinary = buffer.get() != 0; // converting byte to boolean
    boolean isDirectory = buffer.get() != 0; // converting byte to boolean

    FileMetadata metadata = new FileMetadata(currentSizeBytes, isBinary, isDirectory, creationTime, 
                                            lastAccessTime, lastModifiedTime);

    return metadata;

  }

  public int getCurrentSizeBytes() {
      return currentSizeBytes;
  }

  public void setCurrentSizeBytes(int currentSizeBytes) {
     this.currentSizeBytes = currentSizeBytes;
  }
  
  public Instant getCreationTime() {
      return creationTime;
  }
  
  public Instant getLastAccess() {
      return lastAccess;
  }
  
  public Instant getLastModified() {
      return lastModified;
  }
  
  public void setLastModifiedAsNow() {
      this.lastModified = Instant.now();
  }

  public boolean isBinary() {
      return isBinary;
  }
  
  @Override
  public String toString(){
    return "FileMetadata [currentSizeBytes=" + currentSizeBytes + ", isBinary=" + isBinary + ", isDirectory=" + isDirectory + "]";
  }

}
