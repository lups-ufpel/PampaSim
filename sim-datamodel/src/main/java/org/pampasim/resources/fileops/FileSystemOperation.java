package org.pampasim.resources.fileops;

public sealed interface FileSystemOperation
        permits CreateFileOp,
                OpenFileOp,
                CloseFileOp,
                ReadFileOp,
                WriteFileOp,
                CreateDirectoryOp,
                DeleteDirectoryOp {

    int execTime();
}
