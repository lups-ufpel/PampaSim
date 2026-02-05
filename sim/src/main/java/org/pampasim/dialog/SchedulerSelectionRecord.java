package org.pampasim.dialog;

import org.pampasim.memory.dialog.MemoryConfigSelectionRecord;
import org.pampasim.filesystem.FileSystemConfigSelectionRecord;

public record SchedulerSelectionRecord(
        String schedulerName,
        boolean preemptive,
        int quantum,
        MemoryConfigSelectionRecord memoryConfig,
        FileSystemConfigSelectionRecord fileSystemConfig
) {}
