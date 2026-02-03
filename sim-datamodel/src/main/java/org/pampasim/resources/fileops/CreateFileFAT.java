package org.pampasim.resources.fileops;

public record CreateFileFAT(
        int execTime,
        String path
) implements CreateFile {}

