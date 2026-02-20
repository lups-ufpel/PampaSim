package org.pampasim.resources.filesystem.fileops;

public record ReadFileOp(int execTime, String path, int byteNumber, int position) implements FileSystemOperation {}

