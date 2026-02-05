package org.pampasim.filesystem;

public record FileSystemConfigSelectionRecord(
        // disk stuff
        int blockSizeBytes,
        int numberOfBlocks,
        //

        String allocationScheme
) {}
