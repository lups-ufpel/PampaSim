package org.pampasim.resources.fileops;

public record CreateFileContiguous(
        int execTime,
        String path,
        int finalSizeBlocks
) implements CreateFile {}

