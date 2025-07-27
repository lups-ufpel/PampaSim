package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPageReplacementAlgorithm {
    // Collections to track page access patterns
    protected final LinkedList<PageTableEntry> recentlyAccessed = new LinkedList<>();
    protected final LinkedList<PageTableEntry> recentlySwappedIn = new LinkedList<>();
    protected final LinkedList<PageTableEntry> recentlyEvicted = new LinkedList<>();

    // Maximum size for tracking collections
    protected static final int TRACKING_SIZE = 100;

    public abstract ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries);

    public void registerSwapIn(PageTableEntry pageTableEntry) {
        recentlySwappedIn.addFirst(pageTableEntry);
        if (recentlySwappedIn.size() > TRACKING_SIZE) {
            recentlySwappedIn.removeLast();
        }
    }

    public void registerSwapOut(PageTableEntry pageTableEntry) {
        recentlyEvicted.addFirst(pageTableEntry);
        if (recentlyEvicted.size() > TRACKING_SIZE) {
            recentlyEvicted.removeLast();
        }
    }

    public void registerReference(PageTableEntry pageTableEntry) {
        pageTableEntry.setReferenced(true);
        recentlyAccessed.addFirst(pageTableEntry);
        if (recentlyAccessed.size() > TRACKING_SIZE) {
            recentlyAccessed.removeLast();
        }
    }
}