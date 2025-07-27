package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.List;

public class LRU extends AbstractPageReplacementAlgorithm {
    @Override
    public ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries) {
        ArrayList<PageTableEntry> candidates = new ArrayList<>();

        for (int i = recentlyAccessed.size() - 1; i >= 0 && candidates.size() < quantity; i--) {
            PageTableEntry entry = recentlyAccessed.get(i);
            if (pageTableEntries.contains(entry) && entry.isValid()) {
                candidates.add(entry);
            }
        }

        return candidates;
    }
}