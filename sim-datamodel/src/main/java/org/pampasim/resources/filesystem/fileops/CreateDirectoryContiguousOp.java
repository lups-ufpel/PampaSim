package org.pampasim.resources.filesystem.fileops;

public record CreateDirectoryContiguousOp(
        int execTime,
        String path,
        int finalSizeBlocks
) implements CreateDirectoryOp {}

