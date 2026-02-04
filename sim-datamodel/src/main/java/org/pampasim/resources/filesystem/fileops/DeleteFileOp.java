package org.pampasim.resources.filesystem.fileops;

public record DeleteFileOp(int execTime, String path) implements FileSystemOperation {}
