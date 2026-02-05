package org.pampasim.resources.fileops;

public record ReadFileOp(int execTime, String path) implements FileSystemOperation {}

