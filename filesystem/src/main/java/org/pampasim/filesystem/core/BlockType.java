package org.pampasim.filesystem.core;

import javafx.scene.paint.Color;

public enum BlockType{
    EMPTY,
    MBR,
    SUPERBLOCK,
    FREEBLOCKSBITMAP,
    INODEBITMAP,
    INODETABLE,
    INODE,
    FILE,
    DIRECTORY;

    public static Color getCorrespondingColor(BlockType type){
      return switch(type) {
        case MBR -> Color.LIGHTBLUE;
        case EMPTY -> Color.web("#f8f9f3");
        default -> throw new Error("Unhandled block type");
      };

    }
}


