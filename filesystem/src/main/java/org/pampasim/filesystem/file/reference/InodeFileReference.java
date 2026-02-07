package org.pampasim.filesystem.file.reference;

import java.nio.ByteBuffer;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.file.FileMetadata;
import org.pampasim.filesystem.inode.Inode;

public final class InodeFileReference extends FileReference {
  private int inodeIndex;

  public InodeFileReference(int inodeIndex){
    this.inodeIndex = inodeIndex;
  }

  public int getIndex(){
    return inodeIndex;
  }

  @Override
  public void writeToBuffer(ByteBuffer buffer){
    buffer.putInt(inodeIndex);
  }

  public FileMetadata getMetadata(FileSystem fileSystem){
    return Inode.getMetadata(fileSystem, inodeIndex);
  }

  public void setCurrentSizeBytes(FileSystem fileSystem, int currentSizeBytes){
    getMetadata(fileSystem).setCurrentSizeBytes(currentSizeBytes);
  }

  @Override
  public int sizeBytes(){
    int intSizeBytes = Integer.SIZE / 8;
    return intSizeBytes;
  }
  @Override
  public String toString(){
    return "InodeFileReference [inodeIndex=" + inodeIndex +"]";
  }
}
