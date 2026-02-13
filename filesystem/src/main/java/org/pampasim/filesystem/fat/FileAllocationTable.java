package org.pampasim.filesystem.fat;

import java.util.Arrays;

public class FileAllocationTable {
    private int[] fat;
    public static final int UNUSED = -1;
    public static final int EOF = -2;

    public FileAllocationTable(int numberOfBlocks){
        fat = new int[numberOfBlocks];
    }
}
