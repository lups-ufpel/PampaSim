package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.Process;

import java.util.ArrayList;
import java.util.List;

public class FIFOClock extends AbstractPageReplacementAlgorithm {
    private int clockHand = 0;

    @Override
    public ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries) {
        ArrayList<PageTableEntry> candidates = new ArrayList<>();
        int found = 0;

        while (found < quantity && !recentlySwappedIn.isEmpty()) {
            clockHand %= recentlySwappedIn.size();
            PageTableEntry entry = recentlySwappedIn.get(clockHand);

            if (pageTableEntries.contains(entry) && entry.isValid()) {
                if (entry.isReferenced()) {
                    entry.setReferenced(false);
                    clockHand++;
                } else {
                    candidates.add(entry);
                    found++;
                    clockHand++;
                }
            } else {
                clockHand++;
            }
            if (clockHand >= recentlySwappedIn.size()) {
                clockHand = 0;
                if (found == 0) {
                    break;
                }
            }
        }

        while (found < quantity && clockHand < recentlySwappedIn.size()) {
            PageTableEntry entry = recentlySwappedIn.get(clockHand);
            if (pageTableEntries.contains(entry) && entry.isValid()) {
                candidates.add(entry);
                found++;
            }
            clockHand++;
        }

        return candidates;
    }
}