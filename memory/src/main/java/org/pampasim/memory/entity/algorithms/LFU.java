    package org.pampasim.memory.entity.algorithms;

    import org.pampasim.resources.memory.PageTableEntry;
    import org.pampasim.resources.Process;

    import java.util.ArrayList;
    import java.util.Comparator;
    import java.util.List;

    public class LFU extends AbstractPageReplacementAlgorithm {
        @Override
        public ArrayList<PageTableEntry> pickPagesToSwap(int quantity, List<PageTableEntry> pageTableEntries) {
            ArrayList<PageTableEntry> candidates = new ArrayList<>(pageTableEntries);

            candidates.sort(Comparator.comparingInt(entry -> {
                int count = 0;
                for (PageTableEntry e : recentlyAccessed) {
                    if (e.equals(entry)) {
                        count++;
                    }
                }
                return count;
            }));
            return new ArrayList<>(candidates.subList(0, Math.min(quantity, candidates.size())));
        }
    }