package org.pampasim.filesystem;

import org.pampasim.filesystem.core.Disk;

public class Print {

    public static void byteValue(Byte b){
      System.out.println(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
    }

    public static void disk(Disk disk){
      byte[][] fullDisk = disk.debugGetDisk();
      for(int i = 0; i < disk.getNumberOfBlocks(); i++){
        System.out.println("Block " + i + ":\n");
        block(fullDisk[i]);
        System.out.println("\n");
      }
      


    }

    public static void block(byte[] block){
      for(int j = 0; j < block.length; j++)
        {
          byteValue(block[j]);
        }

    }

}
