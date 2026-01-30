package org.pampasim.resources.fileops;

public sealed interface FileSystemOperation
        permits CreateFileOp,
                OpenFileOp,
                CloseFileOp,
                ReadFileOp,
                WriteFileOp,
                DeleteFileOp,
                CreateDirectoryOp,
                DeleteDirectoryOp {

    int execTime();
    String path();
}
