package org.pampasim.resources.dialog;

import java.util.List;

public record ProcessMemoryInfoRecord(
        int processSize,
        List<Boolean> fileBackedPages,
        List<Integer> memoryAccesses,
        List<Boolean> modifiesPageFlags,
        boolean loopAccess                
) { }