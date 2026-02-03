package org.pampasim.resources.fileops;

public record CreateFileInodeOp(
        int execTime,
        String path
) implements CreateFileOp {}

