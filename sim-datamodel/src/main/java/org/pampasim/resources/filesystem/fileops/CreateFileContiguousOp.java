package org.pampasim.resources.filesystem.fileops;

public record CreateFileContiguousOp(
        int execTime,
        String path,
        int maxSizeBytes
) implements CreateFileOp {}

