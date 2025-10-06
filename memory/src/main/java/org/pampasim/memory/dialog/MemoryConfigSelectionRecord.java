package org.pampasim.memory.dialog;

public record MemoryConfigSelectionRecord(
        int pageSize,
        int maxPagesPerProcess,
        int framesInRAM,
        int framesInSwap,
        int swapOperationLength,
        int workingSetWindow,
        String pageSubstitutionAlgorithm,
        boolean globalPageSubstitution,
        boolean anticipatedPageLoading,
        int prePagingRange,
        boolean variablePageAllocation,
        double variablePageAllocationTopThreshold,
        double variablePageAllocationBottomThreshold,
        boolean tlbEnabled,
        int tlbEntries
) {}
