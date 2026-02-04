package org.pampasim.resources.filesystem.fileops;

public record WriteFileOp(int execTime, String path) implements FileSystemOperation {}

