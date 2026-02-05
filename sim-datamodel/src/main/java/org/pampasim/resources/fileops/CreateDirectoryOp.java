package org.pampasim.resources.fileops;

public sealed interface CreateDirectoryOp extends FileSystemOperation
    permits CreateDirectoryContiguousOp,
            CreateDirectoryFATOp,
            CreateDirectoryInodeOp {
}
