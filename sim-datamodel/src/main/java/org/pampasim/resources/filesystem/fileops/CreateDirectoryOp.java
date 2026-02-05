package org.pampasim.resources.filesystem.fileops;

public sealed interface CreateDirectoryOp extends FileSystemOperation
    permits CreateDirectoryContiguousOp,
            CreateDirectoryFATOp,
            CreateDirectoryInodesOp {
}
