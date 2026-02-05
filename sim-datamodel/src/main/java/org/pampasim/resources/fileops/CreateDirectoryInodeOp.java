package org.pampasim.resources.fileops;

public record CreateDirectoryInodeOp(
        int execTime,
        String path
) implements CreateDirectoryOp {}

