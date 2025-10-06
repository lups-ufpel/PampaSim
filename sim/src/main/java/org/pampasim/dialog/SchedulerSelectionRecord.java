package org.pampasim.dialog;

import org.pampasim.memory.dialog.MemoryConfigSelectionRecord;

public record SchedulerSelectionRecord(
        String schedulerName,
        boolean preemptive,
        int quantum,
        MemoryConfigSelectionRecord memoryConfig
) {}