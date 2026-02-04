package org.pampasim.resources.dialog;

import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.filesystem.fileops.FileSystemOperation;

import java.util.List;
import java.util.ArrayList;

public record CreateProcessRecord(
        int start,
        int duration,
        int priority,
        String color,
        ProcessMemoryInfoRecord memoryInfoRecord,
        ArrayList<FileSystemOperation> fileSystemOperations
) { }
