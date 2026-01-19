package org.pampasim.filesystem.mapping;

import java.nio.ByteBuffer;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.file.FileMetadata;
import org.pampasim.filesystem.inode.Inode;

public final class InodeMapping extends FileMapping {
  private int inodeIndex;

  public InodeMapping(int inodeIndex){
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

  @Override
  public int sizeBytes(){
    int intSizeBytes = Integer.SIZE / 8;
    return intSizeBytes;
  }
  @Override
  public String toString(){
    return "InodeMapping [inodeIndex=" + inodeIndex +"]";
  }
}
