package org.pampasim.resources.fileops;

public record CreateFileInode(
        int execTime,
        String path
) implements CreateFile {}

