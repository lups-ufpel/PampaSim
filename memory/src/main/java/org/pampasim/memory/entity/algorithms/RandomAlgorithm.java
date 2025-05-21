package org.pampasim.memory.entity.algorithms;

import org.pampasim.resources.memory.PageTableEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomAlgorithm implements PageReplacementAlgorithm {
    private final Random random = new Random();

    @Override
    public ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries) {
        // Validate input
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (pageTableEntries == null) {
            throw new IllegalArgumentException("Page table entries list cannot be null");
        }
        // Check if there are enough entries
        if (pageTableEntries.size() < quantity) {
            throw new IllegalArgumentException(
                    String.format("Not enough entries to pick (requested %d, available %d)",
                            quantity, pageTableEntries.size())
            );
        }

        // Create a copy of the original list so the original stays intact (just in case)
        ArrayList<PageTableEntry> remainingEntries = new ArrayList<>(pageTableEntries);
        ArrayList<PageTableEntry> selectedEntries = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            int randomIndex = random.nextInt(remainingEntries.size());
            selectedEntries.add(remainingEntries.get(randomIndex));
            remainingEntries.remove(randomIndex);
        }

        return selectedEntries;
    }
}