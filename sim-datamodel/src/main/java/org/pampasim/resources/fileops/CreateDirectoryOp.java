package org.pampasim.resources.fileops;

public record CreateDirectoryOp(int execTime, String path) implements FileSystemOperation {}
