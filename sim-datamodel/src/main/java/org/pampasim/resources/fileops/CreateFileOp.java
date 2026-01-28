package org.pampasim.resources.fileops;

public record CreateFileOp(
        int execTime,
        String path
) implements FileSystemOperation {}
