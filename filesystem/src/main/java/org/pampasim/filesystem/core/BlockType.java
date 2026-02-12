package org.pampasim.filesystem.core;

import javafx.scene.paint.Color;

public enum BlockType{
    EMPTY,
    MBR,
    INITIALIZATION,
    SUPERBLOCK,
    FREE_BLOCKS_BITMAP,
    FREE_INODES_BITMAP,
    INODE_TABLE,
    INODE,
    INODE_INDIRECT_BLOCK,
    FILE,
    DIRECTORY;

    public static Color getCorrespondingColor(BlockType type) {
            return switch (type) {
                case EMPTY ->
                    Color.web("#f8f9f3");
    
                case INITIALIZATION ->
                    Color.web("#fff1c1");
    
                case MBR ->
                    Color.web("#8ecae6");
    
                case SUPERBLOCK ->
                    Color.web("#219ebc");
    
                case FREE_BLOCKS_BITMAP ->
                    Color.web("#b7e4c7");
    
                case FREE_INODES_BITMAP ->
                    Color.web("#95d5b2");
    
                case INODE_TABLE ->
                    Color.web("#ffd166");
    
                case INODE ->
                    Color.web("#f4a261");

                case INODE_INDIRECT_BLOCK ->
                    Color.web("#e9c46a");
    
                case FILE ->
                    Color.web("#e76f51");
    
                case DIRECTORY ->
                    Color.web("#cdb4db");
    
            };

    }

}


