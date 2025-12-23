package org.pampasim.filesystem.mapping;

import java.nio.ByteBuffer;

public final class ContiguousMapping extends FileMapping {
  private int firstBlockIndex;
  private int finalSizeBlocks;
  private FileMetadata metadata;


  public ContiguousMapping(int firstBlockIndex, int finalSizeBlocks, FileMetadata metadata){
    this.firstBlockIndex = firstBlockIndex;
    this.finalSizeBlocks = finalSizeBlocks;
    this.metadata = metadata;
  }

  public FileMetadata getMetadata(){
    return metadata;
  }

  public int getFirstBlockIndex(){
    return firstBlockIndex;
  }

  public int getFinalSizeBlocks(){
    return finalSizeBlocks;
  }

  public int getCurrentSizeBytes(){
    return metadata.getCurrentSizeBytes();
  }

  @Override
  public void writeToBuffer(ByteBuffer buffer){
    buffer.putInt(firstBlockIndex);
    buffer.putInt(finalSizeBlocks);
    metadata.writeToBuffer(buffer);
  }

  @Override
  public int sizeBytes(){
    return super.sizeBytes(AllocationScheme.CONTIGUOUS);
  }


  // assumes relative position of buffer is on start of mapping
  public ContiguousMapping getFromBuffer(ByteBuffer buffer){
    return (ContiguousMapping) super.getFromBuffer(buffer, AllocationScheme.CONTIGUOUS);
  }

  @Override
  public String toString(){
    return "ContiguousMapping [firstBlockIndex=" + firstBlockIndex + ", finalSizeBlocks=" + finalSizeBlocks + ", metadata=" + metadata + "]";
  }

}
