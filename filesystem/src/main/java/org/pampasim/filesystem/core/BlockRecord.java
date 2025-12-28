package org.pampasim.filesystem.core;

public record BlockRecord(
        BlockType type,
        String userString,
        int userInt
) {}
