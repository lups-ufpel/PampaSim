package org.pampasim.resources.fileops;

public record DeleteFile(int execTime, String path) implements FileSystemOperation {}
