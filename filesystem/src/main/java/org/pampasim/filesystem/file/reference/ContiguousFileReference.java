package org.pampasim.filesystem.file.reference;

import org.pampasim.filesystem.file.FileMetadata;
import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.core.FileSystem;

import java.nio.ByteBuffer;

public final class ContiguousFileReference extends FileReference {
  private int firstBlockIndex;
  private int finalSizeBlocks;
  private FileMetadata metadata;


  public ContiguousFileReference(int firstBlockIndex, int finalSizeBlocks, FileMetadata metadata){
    this.firstBlockIndex = firstBlockIndex;
    this.finalSizeBlocks = finalSizeBlocks;
    this.metadata = metadata;
  }

  public FileMetadata getMetadata(){
    return metadata;
  }

  public FileMetadata getMetadata(FileSystem fileSystem){
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

  public void setCurrentSizeBytes(int currentSizeBytes){
    metadata.setCurrentSizeBytes(currentSizeBytes);
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


  // assumes relative position of buffer is on start of reference
  public ContiguousFileReference getFromBuffer(ByteBuffer buffer){
    return (ContiguousFileReference) super.getFromBuffer(buffer, AllocationScheme.CONTIGUOUS);
  }

  @Override
  public String toString(){
    return "Contiguousreference [firstBlockIndex=" + firstBlockIndex + ", finalSizeBlocks=" + finalSizeBlocks + ", metadata=" + metadata + "]";
  }

}
