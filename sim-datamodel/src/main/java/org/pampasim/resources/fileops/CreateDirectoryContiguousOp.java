package org.pampasim.resources.fileops;

public record CreateDirectoryContiguousOp(
        int execTime,
        String path,
        int finalSizeBlocks
) implements CreateDirectoryOp {}

