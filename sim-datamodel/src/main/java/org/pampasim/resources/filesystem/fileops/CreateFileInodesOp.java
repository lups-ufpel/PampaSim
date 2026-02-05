package org.pampasim.resources.filesystem.fileops;

public record CreateFileInodesOp(
        int execTime,
        String path
) implements CreateFileOp {}

