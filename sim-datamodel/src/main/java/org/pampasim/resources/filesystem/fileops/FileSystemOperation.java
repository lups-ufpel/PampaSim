package org.pampasim.resources.filesystem.fileops;

public sealed interface FileSystemOperation
        permits CreateFileOp,
                DeleteFileOp,
                OpenFileOp,
                CloseFileOp,
                ReadFileOp,
                WriteFileOp,
                CreateDirectoryOp,
                DeleteDirectoryOp {
  int execTime();
  String path();
}


