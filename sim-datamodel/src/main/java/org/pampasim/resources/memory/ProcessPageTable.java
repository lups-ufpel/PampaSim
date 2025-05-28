package org.pampasim.resources.memory;

import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ProcessPageTable {
    // page table that stores the page table entries for each process
    private final PageTableEntry[] entries;

    public ProcessPageTable(Process process, int processSize) {
        this.entries = new PageTableEntry[processSize];
        // Initialize all entries
        for (int i = 0; i < processSize; i++) {
            entries[i] = new PageTableEntry(process, i);
        }
    }

    public PageTableEntry getEntry(int pageNumber) {
        if (pageNumber < 0 || pageNumber >= entries.length) {
            throw new IllegalArgumentException("Page Number out of bounds!: " + pageNumber);
        }
        return entries[pageNumber];
    }

    public PageTableEntry[] getEntries(ArrayList<Integer> list) {
        return list.stream()
                .map(this::getEntry)
                .toArray(PageTableEntry[]::new);
    }

    // Update an entry
    public void setEntry(int pageNumber, PageTableEntry pte) {
        if (pageNumber < 0 || pageNumber >= entries.length) {
            throw new IllegalArgumentException("Page Number out of bounds!: " + pageNumber);
        }
        entries[pageNumber] = pte;
    }

    // Get all entries (read-only view)
    public PageTableEntry[] getAllEntries() {
        return entries.clone();  // Defensive copy to prevent external modification
    }

    public ArrayList<PageTableEntry> getValidEntries() {
        return Arrays.stream(entries)
                .filter(PageTableEntry::isValid)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public ArrayList<PageTableEntry> getReferencedEntries() {
        return Arrays.stream(entries)
                .filter(PageTableEntry::isReferenced)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Get the size of the page table
    public int size() {
        return entries.length;
    }
}