package org.pampasim.resources.filesystem.config;

public record FileSystemConfigSelectionRecord(
        // disk stuff
        int blockSizeBytes,
        int numberOfBlocks,
        //

        String allocationScheme
) {}
