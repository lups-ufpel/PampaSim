package org.pampasim.filesystem.core;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;


public class Disk {
    private int blockSizeBytes;
    private int numberOfBlocks;
    private int numberOfReservedBlocks;
    private RandomAccessFile disk; // maybe it would make more sense for there to be several RandomAccessFiles, each being a partition
    private Partition[] partitions;
    private ObservableList<BlockRecord> blockRecords;
    public static final int MAX_PARTITIONS = 10;


    public Disk(int blockSizeBytes, int numberOfBlocks){
        this.blockSizeBytes = blockSizeBytes;
        this.numberOfBlocks = numberOfBlocks;
        this.blockRecords =  FXCollections.observableArrayList();
        for(int i = 0; i < numberOfBlocks; i++){
          blockRecords.add(new BlockRecord(BlockType.EMPTY, "", -1));
        }

        numberOfReservedBlocks = calculateNumberOfReservedBlocks();
        if(numberOfBlocks < numberOfReservedBlocks){
          throw new IllegalArgumentException("Disk is too small for essential information (Master Boot Record) to be stored. Required blocks: " + numberOfReservedBlocks + ". Total number of Blocks: " + numberOfBlocks + ".");
        }

   
        try {
            disk = new RandomAccessFile("disk", "rw");
        } catch (FileNotFoundException e) {
            System.err.println("Error: could not create or open disk file.");
            e.printStackTrace();
        }

        try {
            disk.setLength(0); // clears garbage
            disk.setLength(numberOfBlocks * blockSizeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getMasterBootRecordSizeBytes(){
      int ints_per_partition = 2;
      int int_size_bytes = Integer.SIZE / 8;
      int is_active_byte = 1;
      
      return MAX_PARTITIONS * (int_size_bytes * ints_per_partition + is_active_byte);
    }

    public int calculateNumberOfReservedBlocks(){
      int masterBootRecordBlockNumber = FileSystem.blocksRequiredFor(getMasterBootRecordSizeBytes(), blockSizeBytes);
; 
      numberOfReservedBlocks = masterBootRecordBlockNumber;

      return numberOfReservedBlocks;
    }

    public Partition[] getPartitions(){
      return partitions;
    }

    public void setPartitions(Partition[] partitions){
        if (partitions.length > MAX_PARTITIONS){
            throw new IllegalArgumentException("Too many partitions (max = 10): " + partitions.length);
        }
        for(Partition p : partitions){
          if(p.getLastBlockIndex() > getLastBlockIndex()){
            throw new IllegalArgumentException("Partition goes past disk limit.");
          }
          if(p.getLastBlockIndex() < p.getFirstBlockIndex()){
            throw new IllegalArgumentException("Partition has invalid indexes. First block index: " + p.getFirstBlockIndex() + ". Last block index: " + p.getLastBlockIndex() + ".\n");
          }
        }

        this.partitions = partitions;
        writeMasterBootRecord(partitions);
    } 
    public void writeMasterBootRecord(Partition[] partitions){
      // writes MBR to disk
      int ints_per_partition = 2;
      int int_size_bytes = Integer.SIZE / 8;
      int is_active_byte = 1;

      byte[] masterBootRecord = new byte[getMasterBootRecordSizeBytes()];
      ByteBuffer buffer = ByteBuffer.wrap(masterBootRecord);
      for(int i = 0; i < partitions.length; i++) {
        buffer.putInt(partitions[i].getFirstBlockIndex());
        buffer.putInt(partitions[i].getLastBlockIndex());
        buffer.put( (byte) (partitions[i].isActive() ? 1 : 0));
      }
      masterBootRecord = buffer.array();

      byte[][] masterBootRecordBlocks = FileSystem.splitInBlocks(masterBootRecord, blockSizeBytes);
      for(int i = 0; i < masterBootRecordBlocks.length; i++){
        writeBlock(i, masterBootRecordBlocks[i]);
        setBlockRecord(i, BlockType.MBR);
      }
    }

    public BlockRecord getBlockRecord(int blockIndex){
      return blockRecords.get(blockIndex);
    }

    public void setBlockRecord(int blockIndex, BlockType type, String userString, int userInt){
      blockRecords.set(blockIndex, new BlockRecord(type, userString, userInt));
    }

    public void setBlockRecord(int blockIndex, BlockType type, int userInt){
      setBlockRecord(blockIndex, type, "", userInt);
    }
    
    public void setBlockRecord(int blockIndex, BlockType type, String userString){
      setBlockRecord(blockIndex, type, userString, -1);
    }

    public void setBlockRecord(int blockIndex, BlockType type){
      setBlockRecord(blockIndex, type, "", -1);
    }

    public void setBlockRecord(int firstBlock, int lastBlockExclusive, BlockType type){
      for(int i = firstBlock; i < lastBlockExclusive; i++){
        setBlockRecord(i, type, "", -1);
      }
    }

    // make unmodifiable?
    public ObservableList<BlockRecord> getBlockRecords(){
      return blockRecords;
    }

    //public void createPartition(int initialBlockIndex, int lastBlockIndex){}

    // maybe logical block indexes? Which are added to active partition first block index
    // (probably unecessary)
    public byte[] readBlock(int blockIndex){
        if (!inBounds(blockIndex)){
          throw new IllegalArgumentException("block index out of bounds in disk read");
        }

        byte[] blockBuffer = new byte[blockSizeBytes];
        try {
            disk.seek(blockIndex * blockSizeBytes);
        } catch(IOException e) {
            //TODO: handle
            System.out.println("unhandled");
        }
        try {
            disk.readFully(blockBuffer);
        } catch(IOException e){
            //TODO: handle
            System.out.println("unhandled");
        }

        return blockBuffer;
    }

    public void writeBlock(int blockIndex, byte[] data){
        if (!inBounds(blockIndex)){
          throw new IllegalArgumentException("block index out of bounds in disk write");
        }
        if (data.length != blockSizeBytes){
            System.out.println("Invalid write length.");
            return;
        }
        try {
            disk.seek(blockIndex * blockSizeBytes);
        } catch(IOException e) {
            //TODO: handle
            System.out.println("unhandled");
        }

        try {
            disk.write(data);
        } catch(IOException e){
            //TODO: handle
            System.out.println("unhandled");
        }
    }

    public boolean inBounds(int index){
      return index >= 0 && index < numberOfBlocks;
    }

    public byte[][] debugGetDisk(){
      byte[][] disk = new byte[numberOfBlocks][blockSizeBytes];
      for(int i = 0; i < numberOfBlocks; i++){
        disk[i] = readBlock(i);
      }

      return disk;

    }

    public int getNumberOfBlocks(){
        return numberOfBlocks;
    }

    public int getLastBlockIndex(){
      return numberOfBlocks - 1;
    }

    public int getBlockSizeBytes(){
        return blockSizeBytes;
    }

    public int sizeBytes(){
      return blockSizeBytes * numberOfBlocks;
    }

    public int getNumberOfReservedBlocks(){
        return numberOfReservedBlocks;
    }

    public Partition getActivePartition(){
        for(Partition p : partitions){
            if (p.isActive()){
                return p;
            }
        }

        throw new IllegalStateException("No active partition");
    }
}
