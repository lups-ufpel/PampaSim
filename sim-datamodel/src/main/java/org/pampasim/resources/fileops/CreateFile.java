package org.pampasim.resources.fileops;

public sealed interface CreateFile extends FileSystemOperation
    permits CreateFileContiguous,
            CreateFileFAT,
            CreateFileInode {
}

