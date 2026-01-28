package org.pampasim.resources.fileops;

public record OpenFileOp(
        int execTime,
        String path
) implements FileSystemOperation {}
