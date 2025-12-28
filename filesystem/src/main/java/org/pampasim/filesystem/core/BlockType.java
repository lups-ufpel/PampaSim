package org.pampasim.filesystem.core;

public enum BlockType{
    MBR,
    SUPERBLOCK,
    FREEBLOCKSBITMAP,
    INODEBITMAP,
    INODETABLE,
    INODE,
    FILE,
    DIRECTORY,
}
