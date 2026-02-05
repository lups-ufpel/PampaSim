package org.pampasim.resources.filesystem.fileops;

public record CreateDirectoryInodesOp(
        int execTime,
        String path
) implements CreateDirectoryOp {}

