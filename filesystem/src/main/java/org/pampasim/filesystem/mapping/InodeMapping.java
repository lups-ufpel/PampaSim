package org.pampasim.filesystem.mapping;

import java.nio.ByteBuffer;

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
