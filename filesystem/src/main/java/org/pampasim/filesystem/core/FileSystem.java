package org.pampasim.filesystem.core;

import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.directory.Directory;
import org.pampasim.filesystem.directory.DirectoryEntry;
import org.pampasim.filesystem.file.FileMetadata;
import org.pampasim.filesystem.file.reference.*;
import org.pampasim.filesystem.LegendEntry;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.Event;
import org.pampasim.filesystem.file.File;
import org.pampasim.events.FileSystem.*;
import org.pampasim.resources.filesystem.fileops.*;
import org.pampasim.resources.filesystem.AllocationScheme;
import org.pampasim.filesystem.fat.FileAllocationTable;

import java.nio.BufferUnderflowException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.time.Instant;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class FileSystem extends AbstractSimEntity {

  private Disk disk;
  private Partition partition;
  // addresses on the bit map are relative to partition
  private AllocationBitMap freeBlocksBitMap; // note: little-endian
  private AllocationBitMap inodesBitMap;
  //private Operation[] journal; // needs an inode for itself
  private AllocationScheme allocationScheme;
  private FileAllocationTable fileAllocationTable;
  private Random random = new Random();

  public static final int ROOT_DIRECTORY_INODE_NUMBER = 0;
  public static final float INODES_TO_BYTES_RATIO = (float) (15.0/10000.0);
  public static int INODES_INDEX;
  private static final int FREE_BLOCKS_BITMAP_INDEX = 2;
  private static int FREE_INODES_BITMAP_INDEX; // might not exist
  private static final int ROOT_DIRECTORY_ENTRIES_NUMBER = 5;

  // is here for modularity. different file systems hava different disk requirements
  public final int MINIMUM_PARTITION_SIZE;
  private int root_directory_starting_index;

  public FileSystem(Simulation simulation, Disk disk, Partition partition, AllocationScheme allocationScheme){
      super(simulation);

      this.disk = disk;
      this.allocationScheme = allocationScheme;
      this.partition = partition;

      int totalPartitionBlocks = partition.size(); 
      this.freeBlocksBitMap = new AllocationBitMap(totalPartitionBlocks);
      if(allocationScheme == AllocationScheme.FAT){
        fileAllocationTable = new FileAllocationTable(this, totalPartitionBlocks);
      }

      // maybe too granular. 
      switch(allocationScheme) { // not well thought-out... if inconvenient, delete this
        case INODES:
          MINIMUM_PARTITION_SIZE = 512;
          this.inodesBitMap = new AllocationBitMap(getNumberOfInodes());
          if(disk.getBlockSizeBytes() < Inode.indirectBlockSizeBytes()){
            throw new IllegalStateException("Blocks are too small to fit inode indirect block. Min=" + Inode.indirectBlockSizeBytes());
          }
          break;
        default:
          MINIMUM_PARTITION_SIZE = 512;
          break;
      }
      
      if(partition.size() * disk.getBlockSizeBytes() < MINIMUM_PARTITION_SIZE){
        throw new IllegalStateException("Active partition of size " + partition.size() * disk.getBlockSizeBytes() + " bytes is too small. Minimum for this type of file system is " + MINIMUM_PARTITION_SIZE);
      }


      // register handlers
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.CreateFile.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.DeleteFile.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.OpenFile.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.CloseFile.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.ReadFile.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.WriteFile.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.CreateDirectory.class, this);
      
      simulation.getEventManager().addEventHandler(
              org.pampasim.events.FileSystem.DeleteDirectory.class, this);


  }

  @Override
  public void processEvent(Event event) {
        switch (event) {
            case CreateFile e        -> createFile((CreateFileOp) e.getFileSystemOperation());
            case DeleteFile e        -> deleteFile((DeleteFileOp) e.getFileSystemOperation());
            case OpenFile e          -> openFile();
            case CloseFile e         -> closeFile();
            case ReadFile e          -> readFile();
            case WriteFile e         -> writeFile();
            case CreateDirectory e   -> createDirectory((CreateDirectoryOp) e.getFileSystemOperation());
            case DeleteDirectory e   -> deleteDirectory((DeleteDirectoryOp) e.getFileSystemOperation());
            default -> throw new IllegalStateException(
                    "[FileSystem] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }

  }

  public void createFile(CreateFileOp op){
    switch (op) {
    
        case CreateFileContiguousOp o ->
            File.createContiguous(
                this,
                o.path(),
                0 /* currentSizeBytes */,
                blocksRequiredFor(o.maxSizeBytes()),
                false
            );
    
        case CreateFileFATOp o ->
            File.createFAT(
                this,
                o.path(),
                0 /* currentSizeBytes */,
                false
            );
    
        case CreateFileInodesOp o ->
            File.createInodes(
                this,
                o.path(),
                0 /* currentSizeBytes */,
                false
            );
    };
  }

  public void deleteFile(DeleteFileOp op){
    File.delete(this, op.path());
  }

  public void openFile(){

  }

  public void closeFile(){

  }

  public void readFile(){

  }

  public void writeFile(){
    
  }

  public void createDirectory(CreateDirectoryOp op){
    switch (op) {
    
        case CreateDirectoryContiguousOp o ->
            Directory.createContiguous(
                this,
                o.path(),
                blocksRequiredFor(o.maxSizeBytes())
            );
    
        case CreateDirectoryFATOp o ->
            Directory.createFAT(
                this,
                o.path()
            );
    
        case CreateDirectoryInodesOp o ->
            Directory.createInodes(
                this,
                o.path()
            );
    };
  }

  public void deleteDirectory(DeleteDirectoryOp op){
    Directory.delete(this, op.path());
  }

  public int getNumberOfInodes(){
    return (int) Math.ceil(partition.size() * disk.getBlockSizeBytes() * INODES_TO_BYTES_RATIO);
  }

  public void freeBlocks(int firstBlockIndex, int lastBlockIndex){
    setBlockRecord(firstBlockIndex, lastBlockIndex, BlockType.EMPTY);
    freeBlocksBitMap.setFree(firstBlockIndex, lastBlockIndex);

  }

  public void freeBlock(int blockIndex){
    setBlockRecord(blockIndex, BlockType.EMPTY);
    freeBlocksBitMap.setFree(blockIndex);

  }

  public void freeInode(int inodeIndex){
    inodesBitMap.setFree(inodeIndex);
  }

  public int getRootDirectoryIndex(){
    return switch(allocationScheme){
      case AllocationScheme.INODES -> ROOT_DIRECTORY_INODE_NUMBER;
      case AllocationScheme.CONTIGUOUS -> root_directory_starting_index;
      case AllocationScheme.FAT -> root_directory_starting_index;
    };
  }

  public int[] getFileAllocationTable(){
    return fileAllocationTable.getFAT();
  }

  public FileAllocationTable getBigFileAllocationTable(){
    return fileAllocationTable;
  }

  public ObservableList<BlockRecord> getBlockRecordsReference(){
    return disk.getBlockRecords();
  }

  public boolean isInodeEmpty(int index){
    return Inode.get(this, index).isEmpty();
  }
   
  public Inode[] getInodeTable(){

    Inode[] output = new Inode[getNumberOfInodes()];
    for(int i = 0; i < getNumberOfInodes(); i++){
      output[i] = Inode.get(this, i);
    }

    return output;
  }

  // not used (yet?)
  /*
  public FileReference getRootDirectoryreference(){
    switch(allocationScheme){
      case AllocationScheme.CONTIGUOUS:
      return Directory.getDotFromDisk(root_directory_starting_index, this).getFileReference();
      case AllocationScheme.INODES:
        return Inodereference(ROOT_DIRECTORY_INODE_NUMBER);
      default:
        throw new Error("Not implemented");
    }

  }
  */

  public boolean isFree(int blockIndex){
    return freeBlocksBitMap.get(blockIndex);
  }

  public boolean isInodeFree(int inodeIndex){
    return inodesBitMap.get(inodeIndex);
  }

  public int freeBlocksCount(){
    return freeBlocksBitMap.freeCount();
  }

  public AllocationScheme getAllocationScheme(){
    return allocationScheme;
  }

  public void setAllocated(int fromIndex, int toIndex){
    switch(allocationScheme){
      case INODES: // fall-through
      case CONTIGUOUS:
        freeBlocksBitMap.setAllocated(fromIndex, toIndex);
        break;
      case FAT: // fat doesnt need this
        break;
      default:
        throw new Error("Unhandled switch case");

    }
  }
  
  // missing bound checks
  public void initialize(){
    
    int currentBlock = 0;
    // add to journal: writing initialization block
    writeInitializationBlock(currentBlock);
    setBlockRecord(currentBlock, BlockType.INITIALIZATION);
    currentBlock++;

    // add to journal: writing superBlock
    writeSuperBlock(currentBlock);
    setBlockRecord(currentBlock, BlockType.SUPERBLOCK);
    currentBlock++;


    int sizeBlocks = 0;
    // add to journal: writing freeBlocksBitMap 
    if(allocationScheme != AllocationScheme.FAT){
      sizeBlocks = writeBitMap(freeBlocksBitMap, FREE_BLOCKS_BITMAP_INDEX);
      setBlockRecord(currentBlock, currentBlock + sizeBlocks, BlockType.FREE_BLOCKS_BITMAP);
      currentBlock+= sizeBlocks;
    }
    
    if(allocationScheme == AllocationScheme.INODES){
      // i-nodes bitmap
      FREE_INODES_BITMAP_INDEX = currentBlock;
      sizeBlocks = writeBitMap(inodesBitMap, FREE_INODES_BITMAP_INDEX);
      setBlockRecord(currentBlock, currentBlock + sizeBlocks, BlockType.FREE_INODES_BITMAP);
      currentBlock+= sizeBlocks;

      // inodes
      INODES_INDEX = currentBlock;
      sizeBlocks = writeInodes(currentBlock);
      setBlockRecord(currentBlock, currentBlock + sizeBlocks, BlockType.INODE_TABLE);
      currentBlock+= sizeBlocks;
    }

    if(allocationScheme == AllocationScheme.FAT){
      Arrays.fill(fileAllocationTable.getFAT(), 0, currentBlock, FileAllocationTable.EOF); // prevents from being used by nextFreeBlock
    }  else {

      // needs to be updated for root dir
      freeBlocksBitMap.setAllocated(0, currentBlock);
      writeBitMap(freeBlocksBitMap, FREE_BLOCKS_BITMAP_INDEX);
    }
    
    root_directory_starting_index = currentBlock;
    sizeBlocks = writeRootDirectory(currentBlock);
    setBlockRecord(currentBlock, currentBlock + sizeBlocks, BlockType.DIRECTORY, "/");
    currentBlock+= sizeBlocks;

    if(allocationScheme != AllocationScheme.FAT){
      freeBlocksBitMap.setAllocated(0, currentBlock);
      writeBitMap(freeBlocksBitMap, FREE_BLOCKS_BITMAP_INDEX);
    }
      
  }

  private void writeInitializationBlock(int index){
    byte[] initializationBlock = new byte[disk.getBlockSizeBytes()];
    fillWithRandomData(initializationBlock); // probably will have no use forever
    writeBlock(index, initializationBlock);
  }

  private void writeSuperBlock(int index){
    byte[] superBlock = new byte[disk.getBlockSizeBytes()];
    fillWithRandomData(superBlock); // could be made more realistic later
    writeBlock(index, superBlock);
  }

  private int writeBitMap(AllocationBitMap bitMap, int starting_index){
    int sizeBlocks = blocksRequiredFor(bitMap.getByteArray().length);
    writeBytes(bitMap.getByteArray(), starting_index, 0);

    return sizeBlocks;
  }

  private int writeInodes(int starting_index){
    int sizeBlocks = blocksRequiredFor(getNumberOfInodes() * Inode.sizeBytes());
    // no need to write a bunch of zeros
    //
    return sizeBlocks;
  }

  public int getNextAndAllocate(AllocationBitMap bitMap, int amount, int bitMapStartingIndex){
    int firstFree = bitMap.getContiguousFreeBlocksIndex(amount);
    bitMap.setAllocated(firstFree, firstFree + amount);
    writeBitMap(bitMap, bitMapStartingIndex);

    return firstFree;
  }

  public AllocationBitMap getFreeBlocksBitMap(){
    return freeBlocksBitMap;
  }

  public AllocationBitMap getFreeInodesBitMap(){
    return inodesBitMap;
  }

  // only contiguous allocation asks for >1 block per call, clears the blocks first
  public int getFreeBlocksAndSetAllocated(int numberOfBlocks){
    int output = getNextAndAllocate(freeBlocksBitMap, numberOfBlocks, FREE_BLOCKS_BITMAP_INDEX); 

    byte[] clearedBlock = new byte[getBlockSizeBytes()];
    for(int i = 0; i < output; i++){
      writeBlock(output + i, clearedBlock);
    }

    return output;
  }

  public int nextFreeBlock(){
    switch(allocationScheme){
      case FAT:
        return fileAllocationTable.nextFreeBlock();
                
      case INODES: //fall-through
      case CONTIGUOUS:
        return getFreeBlocksAndSetAllocated(1);

      // i-node does not use this function
      
      default:
        throw new Error("unhandled switch case");
    }
  }

  public int nextFreeInode(){
    return getNextAndAllocate(inodesBitMap, 1, FREE_INODES_BITMAP_INDEX);
  }
  // returns size of root dir
  private int writeRootDirectory(int starting_index){
    Directory root;
    int sizeBlocks = 0;

    int entries = 2;
    int currentSizeBytes = DirectoryEntry.sizeBytes(allocationScheme) * entries;
    sizeBlocks = switch(allocationScheme) {
        case CONTIGUOUS -> blocksRequiredFor(DirectoryEntry.sizeBytes(allocationScheme) * ROOT_DIRECTORY_ENTRIES_NUMBER, disk.getBlockSizeBytes());
        case FAT, INODES -> blocksRequiredFor(DirectoryEntry.sizeBytes(allocationScheme) * entries, disk.getBlockSizeBytes());
    };
    ByteBuffer buffer = ByteBuffer.allocate(currentSizeBytes);

    FileMetadata dotMetadata = new FileMetadata(currentSizeBytes, true, Instant.now());

    FileReference dotReference = switch(allocationScheme){
      case CONTIGUOUS -> new ContiguousFileReference(starting_index, sizeBlocks, dotMetadata);
      case INODES -> new InodeFileReference(ROOT_DIRECTORY_INODE_NUMBER);
      case FAT -> new FATFileReference(starting_index, dotMetadata);
      default -> throw new Error("Unhandled switch case");

    };

    DirectoryEntry dot = new DirectoryEntry(".", dotReference);
    dot.writeToBuffer(buffer);

    FileMetadata dotdotMetadata = new FileMetadata(currentSizeBytes, true, Instant.now());
    FileReference dotdotReference = switch(allocationScheme){
      case CONTIGUOUS -> new ContiguousFileReference(starting_index, sizeBlocks, dotMetadata);
      case INODES -> new InodeFileReference(ROOT_DIRECTORY_INODE_NUMBER);
      case FAT -> new FATFileReference(starting_index, dotMetadata);
      default -> throw new Error("Unhandled switch case");

    };

    DirectoryEntry dotdot = new DirectoryEntry("..", dotdotReference);
    dotdot.writeToBuffer(buffer);

    byte[] rootDirectoryData = buffer.array();
    byte[][] rootDirectoryBlocks = splitInBlocks(rootDirectoryData, disk.getBlockSizeBytes());


    switch(allocationScheme) {

      case CONTIGUOUS: 
      {
        try{
          for(int i = 0; i < rootDirectoryBlocks.length; i++){
            writeBlock(starting_index + i, rootDirectoryBlocks[i]);
          }
        } catch(Exception e){
          throw new Error("partition is too small for root directory");
        }

        break;
      }
      case INODES: 
      {
        Inode rootInode = new Inode(this, dotMetadata);

        for(int i = 0; i < rootDirectoryBlocks.length; i++){
          int next = nextFreeBlock();
          writeBlock(next, rootDirectoryBlocks[i]);
          rootInode.add(next);
        }

        rootInode.writeToDisk();
        break;

      }
      case FAT:
      {
        try{
          int i = 0;
          for(i = 0; i < rootDirectoryBlocks.length; i++){
            writeBlock(starting_index + i, rootDirectoryBlocks[i]);
            if(i != 0){
              // previous block points to new block
              fileAllocationTable.getFAT()[starting_index + (i - 1)] = starting_index + i;
            }
          }
          fileAllocationTable.getFAT()[starting_index + (i - 1)] = FileAllocationTable.EOF;
        } catch(Exception e){
          throw new Error("partition is too small for root directory");
        }
        break;

      }
      default:
        throw new Error("unhandled switch case.");

    }

    buffer.rewind();

    return sizeBlocks;
  }


  public int absoluteAddressOf(int relativeAddress){
    return relativeAddress + partition.getFirstBlockIndex();
  }


  // relative to partition. Checks bounds
  public void writeBlock(int relativeBlockIndex, byte[] data){
    if(relativeBlockIndex + partition.getFirstBlockIndex() > partition.getLastBlockIndex() || relativeBlockIndex < 0){
      throw new IllegalArgumentException("Relative block index " + relativeBlockIndex + " is out of bounds. Partition goes up to " + (partition.size() - 1) + ".");
    }
    disk.writeBlock(partition.getFirstBlockIndex() + relativeBlockIndex, data);
  }

  // relative to partition
  public void setBlockRecord(int relativeBlockIndex, BlockType type, String userString, int userInt){
    disk.setBlockRecord(partition.getFirstBlockIndex() + relativeBlockIndex, type, userString, userInt);
  }

  public void setBlockRecord(int relativeBlockStartIndex, int relativeBlockEndIndex, BlockType type, String userString, int userInt){
    disk.setBlockRecord(partition.getFirstBlockIndex() + relativeBlockStartIndex, partition.getFirstBlockIndex() + relativeBlockEndIndex, type, userString, userInt);
  }

  public void setBlockRecord(int relativeBlockStartIndex, int relativeBlockEndIndex, BlockType type){
    setBlockRecord(relativeBlockStartIndex, relativeBlockEndIndex, type, "", -1);
  }

  public void setBlockRecord(int relativeBlockStartIndex, int relativeBlockEndIndex, BlockType type, String userString){
    setBlockRecord(relativeBlockStartIndex, relativeBlockEndIndex, type, userString, -1);
  }

  public void setBlockRecord(int relativeBlockIndex, BlockType type, int userInt){
    setBlockRecord(relativeBlockIndex, type, "", userInt);
  }

  // relative to partition
  public void setBlockRecord(int relativeBlockIndex, BlockType type){
    setBlockRecord(relativeBlockIndex, type, "", -1);
  }

  public ArrayList<LegendEntry> getAllLegendEntries(){
    var output = new ArrayList<LegendEntry>();

    output.addAll(getFileTreeLegendEntries());
    output.addAll(getIndirectBlockLegendEntries());

    return output;
  }

  public ArrayList<LegendEntry> getIndirectBlockLegendEntries(){
    var output = new ArrayList<LegendEntry>();

    for(BlockRecord b : disk.getBlockRecords()){
      if(b.type() == BlockType.INODE_INDIRECT_BLOCK){
        output.add(new LegendEntry(b));
      }
    }

    return output;
    
  }

  public ArrayList<LegendEntry> getFileTreeLegendEntries(){
    return getDirectoryLegendEntries("/");
  }

  public Directory getRootDirectory(){
    return Directory.find(this, "/");
  }

  public ArrayList<LegendEntry> getDirectoryLegendEntries(String path){

    var output = new ArrayList<LegendEntry>();

    Directory current = null;
    try{
      current = Directory.find(this, path);

    }  catch(BufferUnderflowException e){
      // this function is sometimes called in the middle of the directory being created,
      // since it being called is triggered by diskWrites. ignoring is fine
      return output;
    }

    String name;
    boolean isRoot = path.equals("/");
    if(isRoot){
      name = "/";
    }  else{
      String[] segments = path.split("/");
      name = segments[segments.length - 1];
    }

    output.add(new LegendEntry(new BlockRecord(BlockType.DIRECTORY, path, -1))); 
    for(DirectoryEntry e : current.getEntries().stream().filter(item -> !item.isNull()).toList()){
      if(e.getName().equals(".") || e.getName().equals(".."))
      {
        continue;
      }

      boolean isDirectory = e.getFileReference().getMetadata(this).isDirectory();
      if(isDirectory){
        String subDirectoryPath;
        if(isRoot){
          subDirectoryPath = path + e.getName();
        }  else{
          subDirectoryPath = path + "/" + e.getName();

        }
        output.addAll(getDirectoryLegendEntries(subDirectoryPath));

      }  else{
        output.add(new LegendEntry (new BlockRecord(BlockType.FILE, path + e.getName(), -1))); 
      }


    }

    return output;
  }

  // relative to partition. Checks bounds
  public byte[] readBlock(int relativeBlockIndex){
    if(relativeBlockIndex + partition.getFirstBlockIndex() > partition.getLastBlockIndex() || relativeBlockIndex < 0){
      throw new IllegalArgumentException("Relative block index " + relativeBlockIndex + " is out of bounds. Partition goes up to " + (partition.size() - 1) + ".");
    }
    return disk.readBlock(partition.getFirstBlockIndex() + relativeBlockIndex);
  }

  public byte[][] readBlocks(int relativeFirstBlockIndex, int blockNumber){
    byte[][] blocks = new byte[blockNumber][disk.getBlockSizeBytes()];
    for(int i = 0; i < blockNumber; i++){
      blocks[i] = readBlock(relativeFirstBlockIndex + i);
    }

    return blocks;
  }

  private void fillWithRandomData(byte[] buffer){
    random.nextBytes(buffer);
  }

  public static byte[][] splitInBlocks(byte[] data, int blockSizeBytes){
      int requiredBlocks = blocksRequiredFor(data.length, blockSizeBytes);
      byte[][] blocks = new byte[requiredBlocks][blockSizeBytes];

      for(int i = 0; i < data.length; i++){
        int currentBlock = (int) Math.floor(i / blockSizeBytes);
        blocks[currentBlock][i % blockSizeBytes] = data[i];
      }
      return blocks;
  }

  public static int blocksRequiredFor(int byteNumber, int blockSizeBytes){
    return (int) Math.ceil((float) byteNumber / (float) blockSizeBytes);
  }


  public int blocksRequiredFor(int byteNumber){
    return blocksRequiredFor(byteNumber, getBlockSizeBytes());
  }

  public byte[][] getBytesAsBlocks(int byteNumber, int relative_starting_index){
    int requiredBlocks = blocksRequiredFor(byteNumber, disk.getBlockSizeBytes());
    byte[][] blocks = new byte[requiredBlocks][disk.getBlockSizeBytes()];
    for(int i = 0; i < requiredBlocks; i++){
      blocks[i] = readBlock(relative_starting_index + i);
    }

    return blocks;
  }

  public byte[] readBytes(int byteNumber, int relative_starting_index, int byteOffsetFromBlockStart){

    // chops off unecessary reads/writes
    while(byteOffsetFromBlockStart > disk.getBlockSizeBytes()){
      relative_starting_index++;
      byteOffsetFromBlockStart -= disk.getBlockSizeBytes();
    }

    byte[][] blocks = getBytesAsBlocks(byteNumber + byteOffsetFromBlockStart, relative_starting_index);
    byte[] rawBlocks = flatten(blocks); 

    byte[] output = new byte[byteNumber];

    for(int i = 0; i < byteNumber; i++){
      output[i] = rawBlocks[i + byteOffsetFromBlockStart];
    }

    return output;
  }

  public static byte[] flatten(byte[][] data){
    byte[] flattened = new byte[(data.length == 0) ? 0 : data.length * data[0].length];

    int k = 0;
    for(int i = 0; i < data.length; i++){
      for(int j = 0; j < data[i].length; j++){
        flattened[k] = data[i][j];
        k++;
      }
    }

    return flattened;
  }

  public int getBlockSizeBytes(){
    return disk.getBlockSizeBytes();
  }

  public void writeBytes(byte[] data, int relative_starting_index, int byteOffsetFromBlockStart){

    // chops off unecessary reads/writes
    while(byteOffsetFromBlockStart > disk.getBlockSizeBytes()){
      relative_starting_index++;
      byteOffsetFromBlockStart -= disk.getBlockSizeBytes();
    }

    byte[][] blocks = getBytesAsBlocks(data.length + byteOffsetFromBlockStart, relative_starting_index);
    byte[] rawBlocks = flatten(blocks); 
    byte[][] newBlocks = writeToRawBlocks(rawBlocks, data, byteOffsetFromBlockStart);

    for(int i = 0; i < newBlocks.length; i++){
      writeBlock(relative_starting_index + i, newBlocks[i]);
    }

  }

  public byte[][] writeToRawBlocks(byte[] blocks, byte[] data, int offset){

    for(int i = offset; i < blocks.length; i++){
      if(i - offset < data.length){
        blocks[i] = data[i - offset];
      }  else{
        break;
      }
    }

    byte[][] newBlocks = splitInBlocks(blocks, disk.getBlockSizeBytes());

    return newBlocks;
  }

  public int getFileIndex(String path){
      FileReference reference = getReference(path);

        return switch(allocationScheme){
      case CONTIGUOUS -> ((ContiguousFileReference) reference).getFirstBlockIndex();
      case FAT -> ((FATFileReference) reference).getFirstBlockIndex();
      case INODES -> ((InodeFileReference) reference).getIndex();
      default -> throw new Error("not implemented");
    };

  }

  public FileReference getReference(String path){
    String name;
    if(path.equals("/")){
      name = ".";
    }  else{
      String[] segments = path.split("/");
      name = segments[segments.length - 1];
    }
    return Directory.findParent(this, path).findEntry(name).getFileReference();
  }

  public String findNameByInodeIndex(int index){
    String path = "/";
    return findNameByPath("/", index);
  }

  public String findNameByPath(String path, int index){
    String dirName;
    if(path.equals("/")){
      dirName = "/";
    }  else{
      String[] segments = path.split("/");
      dirName = segments[segments.length - 1];
    }

    Directory current = Directory.find(this, path);
    for(DirectoryEntry e : current.getEntries().stream().filter(item -> !item.isNull()).toList()){

      String entryName = e.getName();
      if(((InodeFileReference) e.getFileReference()).getIndex() == index){
        //prevents returning . which is not helpful
        if(entryName.equals(".")){
          return dirName;
        }  else{
          return entryName;
        }
      }

      // prevents eternal loop
      if(entryName.equals(".") || entryName.equals("..")){
        continue;
      }

      boolean isDirectory = Inode.getMetadata(this, ((InodeFileReference) e.getFileReference()).getIndex()).isDirectory();
      if(isDirectory){

        String subDirectoryPath;
        if(path.equals("/")){
          subDirectoryPath = path + e.getName();
        }  else{
          subDirectoryPath = path + "/" + e.getName();
        }
        String name = findNameByPath(subDirectoryPath, index);
        if(name != null){
          return name;
        }
      }
    }

    return null;
  }

  public FileMetadata findMetadata(String filePath){
    String[] segments = filePath.split("/");
    String name = segments[segments.length - 1];

    FileReference m = getReference(filePath);

    return switch(allocationScheme){
      case INODES -> ((InodeFileReference) m).getMetadata(this);
      case FAT -> ((FATFileReference) m).getMetadata();
      case CONTIGUOUS -> ((ContiguousFileReference) m).getMetadata();
      default -> throw new Error("unhandled switch");
    };
  }
  
}
