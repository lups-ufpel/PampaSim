package org.pampasim.resources.memory;

import org.pampasim.core.utils.PidAllocator;

import java.util.*;

public class PageFrameController {

    // generalized class to represent memory that can be allocated for each process, can be used for virtual addresses, main memory and swapfile

    private final Map<Long, Set<Integer>> processtoPageFrameMap; // Map to store which pages/frame are allocated to which Pids
    private final Map<Integer, Long> pageFrametoProcessMap; // Map to quickly consult which page/frame a process owns
    private final int totalPages; // Total number of page/frames

    public PageFrameController(int totalPages) {
        this.totalPages = totalPages;
        this.processtoPageFrameMap = new HashMap<>();
        this.pageFrametoProcessMap = new HashMap<>();
    }

    public boolean allocatePageFrames(PidAllocator.Pid pid, int startPage, int endPage) {
        if (startPage < 0 || endPage >= totalPages || startPage > endPage) { // validating if the range requested is valid
            return false;
        }

        for(int page = startPage; page <= endPage; page++) { // checking if any page in the range is already allocated
            if(pageFrametoProcessMap.containsKey(page)) {
                return false;
            }
        }

        long pidValue = pid.getId();
        Set<Integer> pages = processtoPageFrameMap.computeIfAbsent(pidValue, k -> new HashSet<>()); // creates an entry in processtoPageFrameMap if one doesn't exist
        // this is only needed if a process tries to allocate more memory after it's creation, which doesn't happen yet in the system's current state

        for (int page = startPage; page <= endPage; page++) { // for each page to be allocated
            pages.add(page); // add to the list of pages owned by the process in processtoPageFrameMap
            pageFrametoProcessMap.put(page, pidValue); // add to the list of pages and who owns them in pageFrametoProcessMap
        }

        return true;
    }

    public void freeProcessPageFrame(PidAllocator.Pid pid) {
        long pidValue = pid.getId();
        Set<Integer> pages = processtoPageFrameMap.remove(pidValue); // remove the entry in processtoPageFrameMap and store the list that was removed
        if (pages != null) {
            for (int page : pages) {
                pageFrametoProcessMap.remove(page); // remove the pages/frames owned by the process in pageFrametoProcessMap
            }
        }
    }

    public void freePageFrame(Integer pageFrame) {
        Long pid = pageFrametoProcessMap.remove(pageFrame);
        if (pid != null) {
            Set<Integer> pages = processtoPageFrameMap.get(pid);
            if (pages != null) {
                pages.remove(pageFrame);
                if (pages.isEmpty()) {
                    processtoPageFrameMap.remove(pid);
                }
            }
        }
    }

    public boolean isPageFrameAllocated(int page) {
        return pageFrametoProcessMap.containsKey(page);
    }

    public Long getPageFrameOwner(int page) {
        return pageFrametoProcessMap.getOrDefault(page, null);
    }

    public Set<Integer> getProcessPageFrame(PidAllocator.Pid pid) {
        return processtoPageFrameMap.getOrDefault(pid.getId(), null);
    }

    public Map<Integer, Long> getAllocatedPageFrames() {
        return new HashMap<>(pageFrametoProcessMap);
    }
    public boolean hasFreePageFrames() {
        return (totalPages - processtoPageFrameMap.size()) > 0;
    }

    public int getTotalAllocatedPageFrames() {
        return pageFrametoProcessMap.size();
    }
    public int getTotalProcessPageFrames(PidAllocator.Pid pid) {
        Set<Integer> frames = processtoPageFrameMap.get(pid.getId());
        return frames != null ? frames.size() : 0;
    }

    public OptionalInt findFirstContiguousFreeRange(int rangeSize) { // finds the first contiguous range of size "rangeSize" and returns the index where the range starts
        if (rangeSize <= 0 || rangeSize > totalPages) { // easy validations
            return OptionalInt.empty();
        }

        int consecutiveFree = 0;
        int startPage = -1;

        for (int page = 0; page < totalPages; page++) {
            if (!pageFrametoProcessMap.containsKey(page)) {
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

    public boolean canAccess(PidAllocator.Pid pid, int pageNumber) { // simple check if a page belongs to a process
        return (pid.getId() == pageFrametoProcessMap.getOrDefault(pageNumber, null));
    }

}
