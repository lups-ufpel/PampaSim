package org.pampasim.resources.memory;

import lombok.Getter;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProcessPageTable {
    // page table that stores the page table entries for each process
    private final ArrayList<PageTableEntry> entries;

    public ProcessPageTable(Process process, int processSize, List<Boolean> fileBackedFlags) {
        this.entries = new ArrayList<>(processSize);
        for (int i = 0; i < processSize; i++) {
            entries.add(new PageTableEntry(process, i, fileBackedFlags.get(i)));
        }
    }

    public PageTableEntry getEntry(int pageNumber) {
        if (pageNumber < 0 || pageNumber >= entries.size()) {
            throw new IllegalArgumentException("Page Number out of bounds!: " + pageNumber);
        }
        return entries.get(pageNumber);
    }

    public ArrayList<PageTableEntry> getEntries(List<Integer> list) {
        return list.stream()
                .map(addr -> this.getEntry(Math.toIntExact(addr)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Update an entry
    public void setEntry(int pageNumber, PageTableEntry pte) {
        if (pageNumber < 0 || pageNumber >= entries.size()) {
            throw new IllegalArgumentException("Page Number out of bounds!: " + pageNumber);
        }
        entries.set(pageNumber, pte);
    }

    // Get all entries (read-only copy)
    public ArrayList<PageTableEntry> getAllEntries() {
        return new ArrayList<>(entries); // Defensive copy
    }

    public ArrayList<PageTableEntry> getValidEntries() {
        return entries.stream()
                .filter(PageTableEntry::isValid)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<PageTableEntry> getInvalidEntries() {
        return entries.stream()
                .filter(entry -> !entry.isValid())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<PageTableEntry> getReferencedEntries() {
        return entries.stream()
                .filter(PageTableEntry::isReferenced)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Get the size of the page table
    public int size() {
        return entries.size();
    }
}
