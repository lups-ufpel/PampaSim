package org.pampasim.resources.fileops;

public record DeleteDirectoryOp(int execTime, String path) implements FileSystemOperation {}
