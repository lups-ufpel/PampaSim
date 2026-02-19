package org.pampasim.filesystem.fat;

import org.pampasim.filesystem.core.FileSystem;

import java.util.Arrays;

public class FileAllocationTable {
    private FileSystem fileSystem;
    private int[] fat;
    public static final int UNUSED = -1;
    public static final int EOF = -2;

    public FileAllocationTable(FileSystem fileSystem, int numberOfBlocks){
        this.fileSystem = fileSystem;
        this.fat = new int[numberOfBlocks];
        Arrays.fill(this.fat, FileAllocationTable.UNUSED);
    }

    public int[] getFAT(){
      return fat;
    }

    public int nextFreeBlock(){
       for (int i = 0; i < fat.length; i++) {
            if (fat[i] == FileAllocationTable.UNUSED) {
                fat[i] = FileAllocationTable.EOF;
                return i;
            }
       }
       throw new Error("no free blocks");
        
    }



    public byte[] readFromFAT(
            int firstBlockIndex,
            int maxBytes,
            int position
    ) {
        int blockSize = fileSystem.getBlockSizeBytes();
        byte[] result = new byte[maxBytes];

        int nextBlock = firstBlockIndex;

        // Determine which block to start from
        int blockOffset = position / blockSize;
        int offsetInsideBlock = position % blockSize;

        // Advance through FAT chain to the starting block
        for (int i = 0; i < blockOffset && nextBlock != FileAllocationTable.EOF; i++) {
            nextBlock = fat[nextBlock];
        }

        int dataPosition = 0;

        while (nextBlock != FileAllocationTable.EOF && dataPosition < maxBytes) {

            byte[] block = fileSystem.readBlock(nextBlock);

            int readableFromBlock = blockSize - offsetInsideBlock;
            int remainingToRead = maxBytes - dataPosition;

            int bytesToCopy = Math.min(readableFromBlock, remainingToRead);

            System.arraycopy(
                    block,
                    offsetInsideBlock,
                    result,
                    dataPosition,
                    bytesToCopy
            );

            dataPosition += bytesToCopy;

            // After first block, always start at offset 0
            offsetInsideBlock = 0;
            nextBlock = fat[nextBlock];
        }

        return result;
    }

    public void writeIntoFAT(byte[] data, int firstBlock, int position){
      int blockSize = fileSystem.getBlockSizeBytes();
      
      int bufferPointer = 0;
      
      // Find starting block and offset
      int blockOffset = position / blockSize;
      int writeOffset = position % blockSize;
      
      int currentBlock = firstBlock;
      
      /* ===============================
         ENSURE FIRST BLOCK EXISTS
         =============================== */
      if (currentBlock == FileAllocationTable.EOF) { // have to update reference?
          currentBlock = fileSystem.nextFreeBlock();
      }
      
      /* ===============================
         TRAVERSE TO STARTING BLOCK
         =============================== */
      for (int i = 0; i < blockOffset; i++) {
      
          if (fat[currentBlock] == FileAllocationTable.EOF) {
              // need to extend chain
              int newBlock = fileSystem.nextFreeBlock();
              fat[newBlock] = FileAllocationTable.EOF;   // reserve
              fat[currentBlock] = newBlock;     // link
          }
      
          currentBlock = fat[currentBlock];
      }
      
      /* ===============================
         WRITE LOOP
         =============================== */
      while (bufferPointer < data.length) {
      
          int writableBytes = blockSize - writeOffset;
          int bytesToWrite = Math.min(
                  writableBytes,
                  data.length - bufferPointer
          );
      
          fileSystem.writeBytes(
                  Arrays.copyOfRange(
                          data,
                          bufferPointer,
                          bufferPointer + bytesToWrite
                  ),
                  currentBlock,
                  writeOffset
          );
      
          bufferPointer += bytesToWrite;
          writeOffset = 0;
      
          if (bufferPointer < data.length) {
              if (fat[currentBlock] == FileAllocationTable.EOF) {
                  int newBlock = fileSystem.nextFreeBlock();
                  fat[newBlock] = FileAllocationTable.EOF;   // reserve immediately
                  fat[currentBlock] = newBlock;     // link
              }

              currentBlock = fat[currentBlock];
          }
      }
  }
}
