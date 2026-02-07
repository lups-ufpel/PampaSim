package org.pampasim.filesystem;

import org.pampasim.filesystem.*;
import org.pampasim.filesystem.core.*;
import org.pampasim.filesystem.directory.*;
import org.pampasim.filesystem.fat.*;
import org.pampasim.filesystem.inode.*;
import org.pampasim.filesystem.file.*;
import org.pampasim.filesystem.file.reference.*;

import java.util.Arrays;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.nio.ByteBuffer;
import java.util.Scanner;

// TODO: create tests for bitmap (already tested but deleted)

/*
public class Main {

    public static void main(String[] args) {

      //testDiskWriting();
      //testFileSystemInitialize(AllocationScheme.INODES);
      //testFileSystemInitialize(AllocationScheme.CONTIGUOUS);
      //testFileMetadataWriting();
      //testFileCreate(AllocationScheme.INODES);
      //testGetDirectoryEntryFromBuffer();
      //testDirectoryHierarchy(AllocationScheme.INODES);
      interact();
      //testInode();
      //testFileWrite(AllocationScheme.CONTIGUOUS);
      //testFileWrite(AllocationScheme.INODES);

    }

    public static void testInode(){
      Disk disk = new Disk(64, 800);
      
      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex(), active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, AllocationScheme.INODES);
      pampaFS.initialize();

      Inode rootDir = Inode.get(pampaFS, 0);
      System.out.println(rootDir);

      byte[] write_ones = new byte[pampaFS.getBlockSizeBytes()];
      Arrays.fill(write_ones, (byte) 1);
      byte[] read_ones = new byte[write_ones.length + 1];

      rootDir.write(pampaFS, write_ones, 0);
      read_ones = rootDir.read(pampaFS, read_ones.length, 0);
      if(!Arrays.equals(read_ones, write_ones)){
        System.out.println("reading too much");

      }

      byte[] write_zeros = new byte[pampaFS.getBlockSizeBytes()];
      Arrays.fill(write_ones, (byte) 0);
      byte[] read_zeros = new byte[write_zeros.length];
      rootDir.write(pampaFS, write_zeros, 4);
      read_ones = rootDir.read(pampaFS, read_zeros.length, 4);
      System.out.println(Arrays.toString(read_zeros));

    }

    public static void interact(){

      Scanner sc = new Scanner(System.in);
      System.out.println("Select disk block size (in Bytes)");
      int diskBlockSizeBytes = sc.nextInt();
      sc.nextLine();
      System.out.println("Select number of blocks");
      int diskBlockNumber = sc.nextInt();
      Disk disk = new Disk(diskBlockSizeBytes, diskBlockNumber);
      
      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex(), active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      System.out.println("Select file system allocation scheme:\n0.Contiguous\n1.I-Nodes");
      int choice = sc.nextInt();
      AllocationScheme as;
      switch(choice){
        case 0:
          as = AllocationScheme.CONTIGUOUS;
          break;
        case 1:
          as = AllocationScheme.INODES;
          break;
        default:
          throw new Error("unhandled");
        
      }
      
      FileSystem pampaFS = new FileSystem(disk, partitionA, as);
      pampaFS.initialize();

      System.out.println("file system initialized.");
      String relativePath = "/";
      int finalSizeBlocks = 0;
      while(true){
        System.out.println("Current position: " + relativePath);
        System.out.println("0.Exit");
        System.out.println("1.Print current directory");
        System.out.println("2.Go to directory");
        System.out.println("3.Create file here");
        System.out.println("4.Create Directory");
        System.out.println("5.Check free Blocks Bit Map");
        System.out.println("6.Print disk blocks");
        if(as == AllocationScheme.INODES){
          System.out.println("7.Check free Inodes Bit Map");
        }
        choice = sc.nextInt();
        sc.nextLine();

        switch(choice){
          case 0:
            return;

          case 1:
            System.out.println("relativePath: " + relativePath);
            System.out.println(Directory.find(pampaFS, relativePath));
            break;

          case 2:
            System.out.println("Type the absolute path:");
            relativePath = sc.nextLine();
            break;

          case 3:
            System.out.println("Type the file name:");
            String name = sc.nextLine();
            switch(as){
              case AllocationScheme.CONTIGUOUS:

                System.out.println("Type the final file size (bytes)");
                finalSizeBlocks = pampaFS.blocksRequiredFor(sc.nextInt());
                //ContiguousFile.create(pampaFS, relativePath + "/" + name, finalSizeBlocks, false, false);
                File.create(pampaFS, relativePath + "/" + name, 10, finalSizeBlocks, false, false);
                break;
              
              case AllocationScheme.INODES:
                File.create(pampaFS, relativePath + "/" + name, false, false);
                break;
            }
            break;

          case 4:
            System.out.println("Type the directory name:");
            String dirName = sc.nextLine();
            switch(as){
              case AllocationScheme.CONTIGUOUS:

                System.out.println("Type the final file size (bytes)");
                finalSizeBlocks = pampaFS.blocksRequiredFor(sc.nextInt());

                Directory.create(pampaFS, relativePath + dirName, finalSizeBlocks);

                break;
              
              case AllocationScheme.INODES:
                Directory.create(pampaFS, relativePath + dirName);
                break;
            }
            break;

          case 5:
          {
            System.out.println("Type the first block index you want to check:");
            int firstBlock = sc.nextInt();
            sc.nextLine();
            System.out.println("Type the last block index you want to check:");
            int lastBlock = sc.nextInt();
            sc.nextLine();
            for(int i = firstBlock; i <= lastBlock && i < partitionA.size(); i++){

              String answer;

              if(pampaFS.isFree(i)){
                answer = "Free";

              }  else{
                answer = "Allocated";

              }
              System.out.println(i + " " + answer + ".");

            }
            break;
          }
          case 6:
          {
            System.out.println("Type the first block index you want to check:");
            int firstBlock = sc.nextInt();
            sc.nextLine();
            System.out.println("Type the last block index you want to check:");
            int lastBlock = sc.nextInt();
            for(int i = firstBlock; i <= lastBlock && i < disk.getNumberOfBlocks(); i++){

              System.out.println("block " + i + ":");
              System.out.println("");

              Print.block(disk.readBlock(i));
              System.out.println("");
            }
            break;
          }

          case 7:
          {
            System.out.println("Type the first inode index you want to check:");
            int firstBlock = sc.nextInt();
            sc.nextLine();
            System.out.println("Type the last inode index you want to check:");
            int lastBlock = sc.nextInt();
            sc.nextLine();
            for(int i = firstBlock; i <= pampaFS.getNumberOfInodes(); i++){

              String answer;

              if(pampaFS.isInodeFree(i)){
                answer = "Free";

              }  else{
                answer = "Allocated";

              }
              System.out.println(i + " " + answer + ".");

            }
            break;
          }

        }
      }
    }


    public static void testFileWrite(AllocationScheme as){
      Disk disk = new Disk(64, 800);

      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex() , active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, as);
      pampaFS.initialize();

      Directory oldRoot = Directory.getFromDisk(pampaFS.getRootDirectoryIndex(), pampaFS);

      //ContiguousFile test = new ContiguousFile(pampaFS);
      File.create(pampaFS, "/a.txt", 10, 10, false, false);
      byte[] writeData = {1,1,1,1};
      File.write(pampaFS, "/a.txt", writeData, 0);
      byte[] readData = File.read(pampaFS, "/a.txt", 4, 0);

      if(!Arrays.equals(writeData, readData)){
        System.out.println("Error!: writeData:" + Arrays.toString(writeData) + "readData:" + Arrays.toString(readData));
      }
      System.out.println(Arrays.toString(writeData) + " " + Arrays.toString(readData));
    }

    public static void testFileCreate(AllocationScheme as){
      Disk disk = new Disk(64, 800);

      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex() , active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, as);
      pampaFS.initialize();

      Directory oldRoot = Directory.getFromDisk(pampaFS.getRootDirectoryIndex(), pampaFS);
      System.out.println(oldRoot);

      File.create(pampaFS, "/a.txt", 10, 10, false, false);
      File.create(pampaFS, "/b.txt", 10, 10, false, false);
      
      Directory newRoot = Directory.getFromDisk(pampaFS.getRootDirectoryIndex(), pampaFS);
      System.out.println(newRoot);
      
    }

    public static void testDirectoryHierarchy(AllocationScheme as){
      Disk disk = new Disk(64, 500);

      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex() , active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, as);
      pampaFS.initialize();

      Directory oldRoot = Directory.getFromDisk(pampaFS.getRootDirectoryIndex(), pampaFS);

      //ContiguousFile test = new ContiguousFile(pampaFS);
      int finalSize = 10;
      int dirSize = 50;
      if(as != AllocationScheme.CONTIGUOUS){
        finalSize = -1;
        dirSize = -1;
      }
      
      System.out.println("a.txt:");
      File.create(pampaFS, "/a.txt", 10, finalSize, false, false);
      System.out.println("/b");
      Directory.create(pampaFS, "/b", dirSize);
      System.out.println("/b/x.txt:");
      File.create(pampaFS, "/b/x.txt", 10, finalSize, false, false);
      System.out.println("/b/c:");
      Directory.create(pampaFS, "/b/c", dirSize);
      File.create(pampaFS, "/b/c/d.txt", 10, finalSize, false, false);
      System.out.println("/b/c/d.txt:");
      
      Directory newRoot = Directory.getFromDisk(pampaFS.getRootDirectoryIndex(), pampaFS);
      System.out.println("Root:");
      System.out.println(newRoot);
      System.out.println("b:");
      System.out.println(Directory.find(pampaFS, "/b"));
      System.out.println("c:");
      System.out.println(Directory.find(pampaFS, "/b/c"));

    }

    public static void testGetDirectoryEntryFromBuffer(){
      Disk disk = new Disk(8, 65);

      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex() , active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, AllocationScheme.CONTIGUOUS);
      pampaFS.initialize();

      int rootDirectoryIndex = pampaFS.getRootDirectoryIndex();
      byte[] dotEntryBytes = pampaFS.readBytes(DirectoryEntry.sizeBytes(AllocationScheme.CONTIGUOUS), rootDirectoryIndex, 0);
      ByteBuffer buffer = ByteBuffer.wrap(dotEntryBytes);
      DirectoryEntry dot = DirectoryEntry.getFromBuffer(buffer, AllocationScheme.CONTIGUOUS);
      System.out.println(dot);


    }

    public static void testDiskWriting(){ 
      byte[] test = {0b00000010, 0x20, 0x20, 0x20, 0x20, 0x20};

      Disk disk = new Disk(test.length, 10);
      boolean active = true;
      Partition partitionA = new Partition(0, 10, active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      AllocationScheme allocationScheme = AllocationScheme.INODES;
      FileSystem pampaFS = new FileSystem(disk, partitionA, allocationScheme);


      disk.writeBlock(0, test);
      byte[] test2 = disk.readBlock(0);
      if(!Arrays.equals(test, test2)){
          System.out.println("error!");
      }
      System.out.println("Written block: \n");
      Print.block(test);
      System.out.println("\n");

      System.out.println("Read block: \n");
      Print.block(test2);

        
    }

    public static void testFileSystemInitialize(AllocationScheme allocationScheme){
      Disk disk = new Disk(64, 200);

      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex() , active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, allocationScheme);
      pampaFS.initialize();

      System.out.println("post file system disk:");
      Print.disk(disk);
      System.out.println(partitionA.getFirstBlockIndex());
      System.out.println(partitionA.getLastBlockIndex());
      
    }

    /*
    public static void testFileMetadataWriting(){

      byte[] test = {0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20, 0x20};

      Disk disk = new Disk(8, 64);
      //System.out.println("pre file system disk:");
      //Print.disk(disk);

      boolean active = true;
      Partition partitionA = new Partition(0 + disk.getNumberOfReservedBlocks(), disk.getLastBlockIndex() , active);
      Partition[] partitions = {partitionA};
      disk.setPartitions(partitions);

      FileSystem pampaFS = new FileSystem(disk, partitionA, AllocationScheme.CONTIGUOUS);
      pampaFS.initialize();
      
      FileMetadata writtenMetadata = new FileMetadata(0, false, false, Instant.now());
      int starting_index = 50;
      // outdated
      writtenMetadata.writeToDisk(disk, starting_index);
      FileMetadata readMetadata = FileMetadata.getFromDisk(disk, starting_index);
      if(writtenMetadata.getCurrentSizeBytes() != readMetadata.getCurrentSizeBytes()){
        System.out.println("Error0");
      }
      
      // epoch milli avoids being unequal due to some milliseconds
      if(writtenMetadata.getCreationTime().toEpochMilli() != readMetadata.getCreationTime().toEpochMilli()){
        System.out.println("Error1");
        System.out.println("written:" + writtenMetadata.getCreationTime() + " read: " + readMetadata.getCreationTime());
      }


      if(writtenMetadata.getLastAccess().toEpochMilli() != readMetadata.getLastAccess().toEpochMilli()){
        System.out.println("Error2");
        System.out.println("written:" + writtenMetadata.getLastAccess() + " read: " + readMetadata.getLastAccess());
      }

      if(writtenMetadata.getLastModified().toEpochMilli() != readMetadata.getLastModified().toEpochMilli()){
        System.out.println("Error3");
        System.out.println("written:" + writtenMetadata.getLastModified() + " read: " + readMetadata.getLastModified());
      }


      if(writtenMetadata.isBinary() != readMetadata.isBinary()){
        System.out.println("Error4");
      }

    }
    

}
*/
