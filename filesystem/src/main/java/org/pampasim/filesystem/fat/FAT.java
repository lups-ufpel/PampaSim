package org.pampasim.filesystem.fat;

import java.util.Arrays;

// file allocation table
public class FAT {
    private int[] fat;
    //private static int endOfFile = -1;

    public FAT(int numberOfBlocks){
        fat = new int[numberOfBlocks];
    }
}
