package org.pampasim.resources.filesystem.fileops;

public record CloseFileOp(int execTime, String path) implements FileSystemOperation {}
