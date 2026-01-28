package org.pampasim.resources.fileops;

public record WriteFileOp(
        int execTime,
        String path
) implements FileSystemOperation {}
