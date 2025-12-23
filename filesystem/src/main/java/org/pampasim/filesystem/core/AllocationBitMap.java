// used for free blocks and inode bit maps
package org.pampasim.filesystem.core;

import java.util.BitSet;

public class AllocationBitMap {
  private BitSet bitMap; // note: little-endian, i.e. backwards;
  public static final boolean allocated = false;
  public static final boolean free = true;
  
  public AllocationBitMap(int sizeBits){
    this.bitMap = new BitSet(sizeBits);
    bitMap.set(0, sizeBits, free);
  }

  public boolean get(int index){
    return bitMap.get(index);
  }

  public void setFree(int index){
    bitMap.set(index, free);
  }

  public void setFree(int fromIndex, int toIndex){
    bitMap.set(fromIndex, toIndex, free);
  }

  public void setAllocated(int index){
    bitMap.set(index, allocated);
  }

  public void setAllocated(int fromIndex, int toIndex){
    bitMap.set(fromIndex, toIndex, allocated);
  }

  public byte[] getByteArray(){
    return bitMap.toByteArray();
  }

  public int length(){
    return bitMap.length();
  }

  public int freeCount(){
    return bitMap.cardinality();
  }

  // looks for a contiguous set of free blocks with a length of at least numberOfBlocks
  public int getContiguousFreeBlocksIndex(int numberOfBlocks){
    int freeBlock = bitMap.nextSetBit(0);
    if(freeBlock == -1){
      throw new IllegalStateException("Active partition does not have " + numberOfBlocks + " contiguous blocks.");
    }
    int nextAllocated = bitMap.nextClearBit(freeBlock);

    while(nextAllocated - freeBlock < numberOfBlocks){
      freeBlock = bitMap.nextSetBit(nextAllocated);
      nextAllocated = bitMap.nextClearBit(freeBlock);
      if(freeBlock == -1){
        throw new IllegalStateException("Active partition does not have " + numberOfBlocks + " contiguous blocks.");
      }
    }

    return freeBlock;
  }
}
