package org.pampasim.resources.filesystem.fileops;

public record DeleteDirectoryOp(int execTime, String path) implements FileSystemOperation {}
