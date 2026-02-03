package org.pampasim.resources.fileops;

public record CloseFile(int execTime, String path) implements FileSystemOperation {}
