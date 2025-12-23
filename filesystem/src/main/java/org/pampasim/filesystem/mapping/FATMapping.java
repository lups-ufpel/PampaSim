package org.pampasim.filesystem.mapping;

import java.nio.ByteBuffer;

public final class FATMapping extends FileMapping {
  private int firstBlockIndex;
  private FileMetadata metadata;


  public FATMapping(int firstBlockIndex, FileMetadata metadata){
    this.firstBlockIndex = firstBlockIndex;
    this.metadata = metadata;
  }

  public FileMetadata getMetadata(){
    return metadata;
  }

  public int getCurrentSizeBytes(){
    return metadata.getCurrentSizeBytes();
  }

  @Override
  public void writeToBuffer(ByteBuffer Buffer){
    throw new Error("FAT write to buffer is not implemented");
  }

  @Override
  public int sizeBytes(){
    throw new Error("not implemented");
  }
}
