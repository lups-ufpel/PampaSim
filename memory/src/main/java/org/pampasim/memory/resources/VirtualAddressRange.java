package org.pampasim.memory.resources;

import org.pampasim.core.utils.PidAllocator;

import java.util.*;

public class VirtualAddressRange {
    // TODO: define a data structure for the virtual addresses
    // For example, if the range of pages is from page 0 to 1024
    // a process will store that it has access to pages 5, 6, 7, 8, 9 of the virtual addresses
    // those pages will be reserved for that process
    //
    // this structure is purely used to handle the allocation of a range of virtual addresses for
    // processes and making sure they are accessing their proper addresses

    // the rest (valid bits, modification bits, etc.) will be handled by the Page Table

    private final Map<Long, Set<Integer>> processPagesMap; // Map to store which pages are allocated to which Pids
    private final Map<Integer, Long> pageToProcessMap; // Map to quickly consult which pages a process owns
    private final int totalPages; // Total number of pages

    public VirtualAddressRange(int totalPages) {
        this.totalPages = totalPages;
        this.processPagesMap = new HashMap<>();
        this.pageToProcessMap = new HashMap<>();
    }

    public boolean allocatePages(PidAllocator.Pid pid, int startPage, int endPage) {
        if (startPage < 0 || endPage >= totalPages || startPage > endPage) { // validating if the range requested is valid
            return false;
        }

        for(int page = startPage; page <= endPage; page++) { // checking if any page in the range is already allocated
            if(pageToProcessMap.containsKey(page)) {
                return false;
            }
        }

        long pidValue = pid.getId();
        Set<Integer> pages = processPagesMap.computeIfAbsent(pidValue, k -> new HashSet<>()); // creates an entry in processPagesMap if one doesn't exist
        // this is only needed if a process tries to allocate more memory after it's creation, which doesn't happen yet in the system's current state

        for (int page = startPage; page <= endPage; page++) { // for each page to be allocated
            pages.add(page); // add to the list of pages owned by the process in processPagesMap
            pageToProcessMap.put(page, pidValue); // add to the list of pages and who owns them in pageToProcessMap
        }

        return true;
    }

    public void freeProcessPages(PidAllocator.Pid pid) {
        long pidValue = pid.getId();
        Set<Integer> pages = processPagesMap.remove(pidValue); // remove the entry in processPagesMap and store the list that was removed
        if (pages != null) {
            for (int page : pages) {
                pageToProcessMap.remove(page); // remove the pages owned by the process in pageToProcessMap
            }
        }
    }

    public boolean isPageAllocated(int page) {
        return pageToProcessMap.containsKey(page);
    }

    public Long getPageOwner(int page) {
        return pageToProcessMap.getOrDefault(page, null);
    }

    public Set<Integer> getProcessPages(PidAllocator.Pid pid) {
        return processPagesMap.getOrDefault(pid.getId(), null);
    }

    public Map<Integer, Long> getAllAllocatedPages() {
        return new HashMap<>(pageToProcessMap);
    }

    public int getTotalAllocatedPages() {
        return pageToProcessMap.size();
    }

    public OptionalInt findFirstContiguousFreeRange(int rangeSize) { // finds the first contiguous range of size "rangeSize" and returns the index where the range starts
        if (rangeSize <= 0 || rangeSize > totalPages) { // easy validations
            return OptionalInt.empty();
        }

        int consecutiveFree = 0;
        int startPage = -1;

        for (int page = 0; page < totalPages; page++) {
            if (!pageToProcessMap.containsKey(page)) {
                if (consecutiveFree == 0) {
                    startPage = page;
                }
                consecutiveFree++;
                if (consecutiveFree == rangeSize) {
                    return OptionalInt.of(startPage);
                }
            } else {
                consecutiveFree = 0;
            }
        }

        return OptionalInt.empty(); // No range of that size found
    }

}
