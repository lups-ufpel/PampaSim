package org.pampasim.resources.fileops;

public record CreateFileFATOp(
        int execTime,
        String path
) implements CreateFileOp {}

