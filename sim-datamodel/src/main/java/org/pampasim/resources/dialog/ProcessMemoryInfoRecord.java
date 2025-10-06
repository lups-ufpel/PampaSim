package org.pampasim.resources.dialog;

import java.util.ArrayList;
import java.util.List;

public record ProcessMemoryInfoRecord(
        int processSize,
        ArrayList<Boolean> fileBackedPages,
        ArrayList<Integer> memoryAccesses,
        ArrayList<Boolean> modifiesPageFlags,
        boolean loopAccess                
) { }