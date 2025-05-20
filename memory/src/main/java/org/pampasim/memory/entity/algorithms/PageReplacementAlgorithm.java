package org.pampasim.memory.entity.algorithms;

import org.pampasim.core.utils.PidAllocator;
import org.pampasim.memory.entity.PageTableManager;
import org.pampasim.resources.memory.PageTableEntry;

import java.util.List;

public abstract class PageReplacementAlgorithm {
    boolean globalReplacementPolicy;
    PageTableManager pageTableManager; // to access all the page table entries
    // this feels wrong, but I don't see any other solutions atm for checking every process in a global replacement policy

    PageReplacementAlgorithm(PageTableManager pageTableManager, boolean globalReplacementPolicy) {
        this.pageTableManager = pageTableManager;
        this.globalReplacementPolicy = globalReplacementPolicy;
    }

    public abstract List<PageTableEntry> pickPagesToSwap(int quantity);
}
