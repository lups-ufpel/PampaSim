package org.pampasim.resources.filesystem.fileops;

public record CreateFileFATOp(
        int execTime,
        String path
) implements CreateFileOp {}

