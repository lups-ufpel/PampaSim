package org.pampasim.resources.dialog;

import org.pampasim.resources.memory.ProcessMemoryInfo;

import java.util.List;

public record CreateProcessRecord(
        int start,
        int duration,
        int priority,
        String color,
        ProcessMemoryInfoRecord memoryInfoRecord
) { }
