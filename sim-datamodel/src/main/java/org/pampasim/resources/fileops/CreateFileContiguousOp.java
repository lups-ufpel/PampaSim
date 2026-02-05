package org.pampasim.resources.fileops;

public record CreateFileContiguousOp(
        int execTime,
        String path,
        int finalSizeBlocks
) implements CreateFileOp {}

