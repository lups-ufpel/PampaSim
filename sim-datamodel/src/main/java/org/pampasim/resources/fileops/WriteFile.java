package org.pampasim.resources.fileops;

public record WriteFile(int execTime, String path) implements FileSystemOperation {}

