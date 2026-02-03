package org.pampasim.filesystem.mapping;

import org.pampasim.filesystem.file.FileMetadata;

import java.nio.ByteBuffer;

public final class FATMapping extends FileMapping {
  public static final int FIRST_BLOCK_NOT_SET = 0;
  private int firstBlockIndex;
  private FileMetadata metadata;


  public FATMapping(int firstBlockIndex, FileMetadata metadata){
    this.firstBlockIndex = firstBlockIndex;
    this.metadata = metadata;
  }

  public FileMetadata getMetadata(){
    return metadata;
  }

  public void setFirstBlockIndex(int firstBlockIndex){
    this.firstBlockIndex = firstBlockIndex;
  }

  public int getCurrentSizeBytes(){
    return metadata.getCurrentSizeBytes();
  }

  public void setCurrentSizeBytes(int currentSizeBytes){
    metadata.setCurrentSizeBytes(currentSizeBytes);
  }

  public int getFirstBlockIndex(){
    return firstBlockIndex;
  }

  @Override
  public void writeToBuffer(ByteBuffer buffer){
    buffer.putInt(firstBlockIndex);
    metadata.writeToBuffer(buffer);
  }

  @Override
  public int sizeBytes(){
    throw new Error("not implemented");
  }
}
