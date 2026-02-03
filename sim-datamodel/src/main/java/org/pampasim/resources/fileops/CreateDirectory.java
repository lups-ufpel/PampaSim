package org.pampasim.resources.fileops;

public record CreateDirectory(int execTime, String path) implements FileSystemOperation {}
