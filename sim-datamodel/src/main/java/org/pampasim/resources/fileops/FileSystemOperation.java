package org.pampasim.resources.fileops;

public sealed interface FileSystemOperation
        permits CreateFile,
                DeleteFile,
                OpenFile,
                CloseFile,
                ReadFile,
                WriteFile,
                CreateDirectory,
                DeleteDirectory {
  int execTime();
  String path();
}


