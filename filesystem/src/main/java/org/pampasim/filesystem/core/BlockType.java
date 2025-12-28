package org.pampasim.filesystem.core;

public enum BlockType{
    EMPTY,
    MBR,
    SUPERBLOCK,
    FREEBLOCKSBITMAP,
    INODEBITMAP,
    INODETABLE,
    INODE,
    FILE,
    DIRECTORY,
}
