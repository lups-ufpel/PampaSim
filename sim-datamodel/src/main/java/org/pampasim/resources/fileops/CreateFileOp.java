package org.pampasim.resources.fileops;

public sealed interface CreateFileOp extends FileSystemOperation
    permits CreateFileContiguousOp,
            CreateFileFATOp,
            CreateFileInodeOp {
}

