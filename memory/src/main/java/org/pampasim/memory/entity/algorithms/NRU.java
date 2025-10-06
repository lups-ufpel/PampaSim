package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.List;

public class NRU extends AbstractPageReplacementAlgorithm {
    @Override
    public ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries) {
        ArrayList<PageTableEntry> candidates = new ArrayList<>();

        // Class 0: not referenced, not dirty
        // Class 1: not referenced, dirty
        // Class 2: referenced, not dirty
        // Class 3: referenced, dirty

        for (int classNum = 0; classNum <= 3 && candidates.size() < quantity; classNum++) {
            for (PageTableEntry entry : pageTableEntries) {
                if (!entry.isValid()) continue;

                boolean matchesClass = false;
                switch (classNum) {
                    case 0: matchesClass = !entry.isReferenced() && !entry.isDirty(); break;
                    case 1: matchesClass = !entry.isReferenced() && entry.isDirty(); break;
                    case 2: matchesClass = entry.isReferenced() && !entry.isDirty(); break;
                    case 3: matchesClass = entry.isReferenced() && entry.isDirty(); break;
                }
                if (matchesClass) {
                    candidates.add(entry);
                    if (candidates.size() >= quantity) {
                        break;
                    }
                }
            }
        }
        return candidates;
    }
}