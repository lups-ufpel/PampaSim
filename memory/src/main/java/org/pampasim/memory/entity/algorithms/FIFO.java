package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.List;

public class FIFO extends AbstractPageReplacementAlgorithm {
    @Override
    public void registerSwapIn(PageTableEntry pageTableEntry) {
        recentlySwappedIn.addLast(pageTableEntry);
        if (recentlySwappedIn.size() > TRACKING_SIZE) {
            recentlySwappedIn.removeFirst();
        }
    }

    @Override
    public ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries) {
        ArrayList<PageTableEntry> candidates = new ArrayList<>();
        for (PageTableEntry entry : recentlySwappedIn) {
            if (pageTableEntries.contains(entry) && entry.isValid()) {
                candidates.add(entry);
                if (candidates.size() >= quantity) {
                    break;
                }
            }
        }
        return candidates;
    }
}