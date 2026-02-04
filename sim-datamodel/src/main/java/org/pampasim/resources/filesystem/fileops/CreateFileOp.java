package org.pampasim.resources.filesystem.fileops;

public sealed interface CreateFileOp extends FileSystemOperation
    permits CreateFileContiguousOp,
            CreateFileFATOp,
            CreateFileInodesOp {
}

