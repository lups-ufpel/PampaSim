package org.pampasim.filesystem.mapping;

import org.pampasim.filesystem.file.FileMetadata;

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
  public void writeToBuffer(ByteBuffer buffer){
    buffer.putInt(firstBlockIndex);
    metadata.writeToBuffer(buffer);
  }

  @Override
  public int sizeBytes(){
    throw new Error("not implemented");
  }
}
