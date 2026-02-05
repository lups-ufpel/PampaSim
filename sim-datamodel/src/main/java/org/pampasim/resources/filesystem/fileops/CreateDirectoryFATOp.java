package org.pampasim.resources.filesystem.fileops;

public record CreateDirectoryFATOp(
        int execTime,
        String path
) implements CreateDirectoryOp {}

