package org.pampasim.resources.fileops;

public record CreateDirectoryFATOp(
        int execTime,
        String path
) implements CreateDirectoryOp {}

