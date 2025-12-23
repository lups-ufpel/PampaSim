package org.pampasim.filesystem.mapping;

import java.nio.ByteBuffer;
abstract public class FileMapping {

  abstract public void writeToBuffer(ByteBuffer buffer);
  abstract public int sizeBytes();

  public static int sizeBytes(AllocationScheme allocationScheme){
    int bitsInBytes = 8;
    int intSizeBytes = (Integer.SIZE / bitsInBytes); 
    int totalInts;

    switch(allocationScheme){
      case AllocationScheme.CONTIGUOUS:
        totalInts = 2;
        return (intSizeBytes * totalInts) + FileMetadata.sizeBytes();
      case AllocationScheme.INODES:
        totalInts = 1;
        return (intSizeBytes * totalInts);
        
      default:
        return -1;
    }
  }

  public static FileMapping getFromBuffer(ByteBuffer buffer, AllocationScheme allocationScheme){

    switch(allocationScheme){
      case CONTIGUOUS:
      {
        int firstBlockIndex = buffer.getInt();
        int finalSizeBlocks = buffer.getInt();
        FileMetadata metadata = FileMetadata.getFromBuffer(buffer);

        return new ContiguousMapping(firstBlockIndex, finalSizeBlocks, metadata);
      }

      case INODES:
      {
        return new InodeMapping(buffer.getInt());
      }

      case FAT:
        throw new Error("unhandled");

      default:
        {
        int firstBlockIndex = buffer.getInt();
        int finalSizeBlocks = buffer.getInt();
        FileMetadata metadata = FileMetadata.getFromBuffer(buffer);

        return new ContiguousMapping(firstBlockIndex, finalSizeBlocks, metadata);
        }

    }
  }
}
