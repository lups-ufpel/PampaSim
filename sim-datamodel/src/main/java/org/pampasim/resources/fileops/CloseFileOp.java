package org.pampasim.resources.fileops;

public record CloseFileOp(
        int execTime,
        String path
) implements FileSystemOperation {}
