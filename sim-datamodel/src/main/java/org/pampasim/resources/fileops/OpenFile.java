package org.pampasim.resources.fileops;

public record OpenFile(int execTime, String path) implements FileSystemOperation {}
