package org.pampasim.filesystem.mapping;

import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.file.FileMetadata;

import java.nio.ByteBuffer;
abstract public class FileMapping {

  abstract public void writeToBuffer(ByteBuffer buffer);
  abstract public int sizeBytes();

  public static int sizeBytes(AllocationScheme allocationScheme){
    int bitsInBytes = 8;
    int intSizeBytes = (Integer.SIZE / bitsInBytes); 
    int totalInts;

    switch(allocationScheme){
      case CONTIGUOUS:
        totalInts = 2;
        return (intSizeBytes * totalInts) + FileMetadata.sizeBytes();
      case FAT: // fall-through
        totalInts = 1;
        return (intSizeBytes * totalInts) + FileMetadata.sizeBytes();
      case INODES:
        totalInts = 1;
        return (intSizeBytes * totalInts);
        
      default:
        throw new Error("unhandled switch");
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

      case FAT:
      {
        int firstBlockIndex = buffer.getInt();
        FileMetadata metadata = FileMetadata.getFromBuffer(buffer);

        return new FATMapping(firstBlockIndex, metadata);
      }

      case INODES:
      {
        return new InodeMapping(buffer.getInt());
      }


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
