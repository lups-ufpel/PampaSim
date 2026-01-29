package org.pampasim.resources.fileops;

public record DeleteFileOp(
        int execTime,
        String path
) implements FileSystemOperation {}
