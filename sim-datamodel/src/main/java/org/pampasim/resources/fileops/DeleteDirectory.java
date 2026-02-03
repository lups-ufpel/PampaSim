package org.pampasim.resources.fileops;

public record DeleteDirectory(int execTime, String path) implements FileSystemOperation {}
