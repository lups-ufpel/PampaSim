package org.pampasim.resources.filesystem.fileops;

public record OpenFileOp(int execTime, String path) implements FileSystemOperation {}
