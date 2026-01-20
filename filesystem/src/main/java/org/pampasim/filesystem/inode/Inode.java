package org.pampasim.filesystem.inode;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.file.FileMetadata;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Inode{
  private FileMetadata metadata;
  private int[] directAddresses;
  private int singlyIndirectPointer = 0; // points to block containing "indirectBlocks"
  private int[] indirectAddresses; // 12 indirect disk addresses (not actually part of the inode)
  private int index; // not actually part of inode
  private FileSystem fileSystem; 
  public static final int ADDRESSES_NUMBER = 12; // same as Unix
  private static final int EMPTY = 0; // since block 0 is reserved, we can say point to block 0 to signal no block

  public Inode(FileSystem fileSystem, FileMetadata metadata){
    this.fileSystem = fileSystem;
    this.metadata = metadata;
    index = fileSystem.nextFreeInode();
    directAddresses = new int[ADDRESSES_NUMBER];
    indirectAddresses = new int[ADDRESSES_NUMBER];
  }

  public Inode(FileSystem fileSystem, int index, FileMetadata metadata, int[] directAddresses, int singlyIndirectPointer, int[] indirectAddresses){

    this.fileSystem = fileSystem;
    this.index = index;
    this.metadata = metadata;
    this.directAddresses = directAddresses;
    this.singlyIndirectPointer = singlyIndirectPointer;
    this.indirectAddresses = indirectAddresses;
  }

  public void writeToBuffer(ByteBuffer buffer){
    metadata.writeToBuffer(buffer);
    for(int i : directAddresses){
      buffer.putInt(i);
    }
    buffer.putInt(singlyIndirectPointer);
  }

  // does not count indirect blocks, as they are not part of the main inode "head"
  public static int sizeBytes(){ 
    int bitsInBytes = 8;
    int intSizeBytes = Integer.SIZE / bitsInBytes;
    int indirectPointers = 1;
    return FileMetadata.sizeBytes() * intSizeBytes * (ADDRESSES_NUMBER + indirectPointers);
  }

  public static int maxDataSizeBytes(FileSystem fileSystem){
    return (ADDRESSES_NUMBER * 2) * fileSystem.getBlockSizeBytes();
  }

  public int getIndex(){
    return index;
  }

  public FileMetadata getMetadata(){
    return metadata;
  }

  public boolean isEmpty(){
    return directAddresses[0] == EMPTY;
  }

  public void add(int blockIndex){
    for(int i = 0; i < ADDRESSES_NUMBER; i++){
      //System.out.println(directAddresses[i]);
      //System.out.println(i);
      if (directAddresses[i] == EMPTY){
        directAddresses[i] = blockIndex;
        //System.out.println("direct address " + i + "equals " + blockIndex);
        return;
      }
      //System.out.println("");
    }

    if(singlyIndirectPointer == EMPTY){
      singlyIndirectPointer = createIndirectBlock();
    }


    for(int i = 0; i < ADDRESSES_NUMBER; i++){
      if(indirectAddresses[i] == EMPTY){
        indirectAddresses[i] = blockIndex;
        return;
      }
    }


  }

  private int[] allAddresses(){
    int[] result = new int[directAddresses.length + indirectAddresses.length];
    System.arraycopy(directAddresses, 0, result, 0, directAddresses.length);
    System.arraycopy(indirectAddresses, 0, result, directAddresses.length, indirectAddresses.length);
    return result;
  }

  public byte[] read(FileSystem fileSystem, int byteNumber, int position){
    int blockOffset = 0;
    int blockSizeBytes = fileSystem.getBlockSizeBytes();
    while(position > blockSizeBytes){
      blockOffset++;
      position -= blockSizeBytes;
    }

    int[] allAddresses = allAddresses();

    byte[] data = new byte[fileSystem.blocksRequiredFor(byteNumber) * fileSystem.getBlockSizeBytes()];
    int readBytes = 0;
    for(int i = 0; i < fileSystem.blocksRequiredFor(byteNumber); i++){
      if(allAddresses[blockOffset + i] == EMPTY){
        break;
      }

      byte[] readBlock = fileSystem.readBlock(allAddresses[blockOffset + i]);
      System.arraycopy(readBlock, 0, data, readBytes, blockSizeBytes);
      readBytes += blockSizeBytes;
    }

    // may have less bytes to read than requested byteNumber
    int endOfRange = Math.min(readBytes, byteNumber + position);
    if(position > endOfRange){
      return new byte[0];
    }

    return Arrays.copyOfRange(data, position, endOfRange);
  }

  public static byte[] read(FileSystem fileSystem, int inode_index, int byteNumber, int position){
    return get(fileSystem, inode_index).read(fileSystem, byteNumber, position);
  }

  public void write(FileSystem fileSystem, byte[] data, int position){

    if(position + data.length > metadata.getCurrentSizeBytes()){
      metadata.setCurrentSizeBytes(position + data.length);
    }
    metadata.setLastModifiedAsNow();

    int readBytes = fileSystem.blocksRequiredFor(data.length + position) * fileSystem.getBlockSizeBytes();

    int blockOffset = 0;
    int positionInWriteBlocks = position;
    while(positionInWriteBlocks > fileSystem.getBlockSizeBytes()){
      blockOffset++;
      positionInWriteBlocks -= fileSystem.getBlockSizeBytes();
    }

    byte[] rawBlocks = read(fileSystem, readBytes, blockOffset * fileSystem.getBlockSizeBytes());
    // adds 0s if necessary
    byte[] rawBlocksPadded = new byte[readBytes];
    if(rawBlocks.length < readBytes){
      System.arraycopy(rawBlocks, 0, rawBlocksPadded, 0, rawBlocks.length);
    }


    byte[][] blocks = fileSystem.writeToRawBlocks(rawBlocksPadded, data, positionInWriteBlocks);

    for(int i = blockOffset; i < blocks.length; i++){
      int blockToWriteIndex = i - blockOffset;
      byte[] blockToWrite = blocks[blockToWriteIndex];
      if(blockToWriteIndex < ADDRESSES_NUMBER){
        writeToAddressArray(directAddresses, i, blockToWrite);
      }  else {
        if(singlyIndirectPointer == EMPTY){
          singlyIndirectPointer = createIndirectBlock();
        }
        int indirectIndex = i - ADDRESSES_NUMBER;
        writeToAddressArray(indirectAddresses, indirectIndex, blockToWrite);
        }
    }

    writeToDisk();
  }


  private void writeToAddressArray(int[] array, int index, byte[] block){
      if(array[index] == EMPTY){
        array[index] = fileSystem.nextFreeBlock();
      }
      fileSystem.writeBlock(array[index], block);
  }

  public int createIndirectBlock(){

    ByteBuffer indirectBlock = ByteBuffer.allocate(indirectBlockSizeBytes());
    int indirectPointer = fileSystem.nextFreeBlock();

    fileSystem.writeBlock(indirectPointer, indirectBlock.array());

    return indirectPointer;
  }

  public void writeToDisk(){
    ByteBuffer buffer = ByteBuffer.allocate(sizeBytes());
    metadata.writeToBuffer(buffer);

    putAddresses(buffer, directAddresses);

    buffer.putInt(singlyIndirectPointer);

    //writes main inode block
    fileSystem.writeBytes(buffer.array(), fileSystem.INODES_INDEX, sizeBytes() * index);

    ByteBuffer indirectBuffer = ByteBuffer.allocate(indirectBlockSizeBytes());
    putAddresses(indirectBuffer, indirectAddresses);

    fileSystem.writeBytes(indirectBuffer.array(), singlyIndirectPointer, 0);


  }

  public static void putAddresses(ByteBuffer buffer, int[] addresses){
    for(int i = 0; i < addresses.length; i++){
      buffer.putInt(addresses[i]);
    }
  }

  public static int indirectBlockSizeBytes(){
    int bitsInBytes = 8;
    return (Integer.SIZE / bitsInBytes) * ADDRESSES_NUMBER;

  }

  public static void getAddressesFromBuffer(ByteBuffer buffer, int[] addressArray){
    for(int i = 0; i < ADDRESSES_NUMBER; i++){
      addressArray[i] = buffer.getInt();
    }
  }

  public static Inode get(FileSystem fileSystem, int inodeIndex){
    int inodePositionFromStart = sizeBytes() * inodeIndex;
    ByteBuffer buffer = ByteBuffer.wrap(fileSystem.readBytes(sizeBytes(), fileSystem.INODES_INDEX, inodePositionFromStart));
    FileMetadata metadata = FileMetadata.getFromBuffer(buffer);
    int[] directAddresses = new int[ADDRESSES_NUMBER];
    getAddressesFromBuffer(buffer, directAddresses);

    int singlyIndirectPointer = buffer.getInt();
    int[] indirectAddresses = new int[ADDRESSES_NUMBER];
    if(singlyIndirectPointer != 0){
      int bitsInBytes = 8;
      ByteBuffer indirectBlock = ByteBuffer.wrap(fileSystem.readBytes((Integer.SIZE / bitsInBytes) * ADDRESSES_NUMBER, singlyIndirectPointer, 0));
      getAddressesFromBuffer(indirectBlock, indirectAddresses);
    }


    return new Inode(fileSystem, inodeIndex, metadata, directAddresses, singlyIndirectPointer, indirectAddresses);
  }

  public static FileMetadata getMetadata(FileSystem fileSystem, int index){
    ByteBuffer buffer = ByteBuffer.wrap(fileSystem.readBytes(FileMetadata.sizeBytes(), fileSystem.INODES_INDEX, sizeBytes() * index));

    return FileMetadata.getFromBuffer(buffer);
  }

  @Override
  public String toString(){
    return "INode (index " + index + ") " + "[metadata=" + metadata + ", directAddresses=" + Arrays.toString(directAddresses) + ", singlyIndirectPointer=" + singlyIndirectPointer + ", indirectAddresses=" + Arrays.toString(indirectAddresses) + "]";

  }


}
