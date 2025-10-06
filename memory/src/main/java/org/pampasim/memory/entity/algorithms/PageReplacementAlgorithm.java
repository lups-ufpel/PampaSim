package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;

import java.util.ArrayList;
import java.util.List;

public interface PageReplacementAlgorithm {
    ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries);
    void registerSwapIn(PageTableEntry pageTableEntry);
    void registerSwapOut(PageTableEntry pageTableEntry);
    void registerReference(PageTableEntry pageTableEntry);
}
