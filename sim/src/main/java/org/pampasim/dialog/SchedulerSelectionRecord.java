package org.pampasim.dialog;

import org.pampasim.memory.dialog.MemoryConfigSelectionRecord;
import org.pampasim.resources.filesystem.config.FileSystemConfigSelectionRecord;

public record SchedulerSelectionRecord(
        String schedulerName,
        boolean preemptive,
        int quantum,
        MemoryConfigSelectionRecord memoryConfig,
        FileSystemConfigSelectionRecord fileSystemConfig
) {}
