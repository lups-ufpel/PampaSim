package org.pampasim.resources.memory;

import lombok.Getter;
import org.pampasim.core.utils.PidAllocator;

import java.util.*;

public class FrameController {

    // generalized class to represent memory that can be allocated for each process, can be used for virtual addresses, main memory and swapfile

    private final Map<Long, Set<Integer>> processtoFrameMap; // Map to store which pages/frame are allocated to which Pids
    private final Map<Integer, Long> frametoProcessMap; // Map to quickly consult which page/frame a process owns
    @Getter
    private final int totalPages; // Total number of page/frames

    public FrameController(int totalPages) {
        this.totalPages = totalPages;
        this.processtoFrameMap = new HashMap<>();
        this.frametoProcessMap = new HashMap<>();
    }

    public boolean allocateFrames(PidAllocator.Pid pid, int startPage, int endPage) {
        if (startPage < 0 || endPage >= totalPages || startPage > endPage) { // validating if the range requested is valid
            return false;
        }

        for(int page = startPage; page <= endPage; page++) { // checking if any page in the range is already allocated
            if(frametoProcessMap.containsKey(page)) {
                return false;
            }
        }

        long pidValue = pid.getId();
        Set<Integer> pages = processtoFrameMap.computeIfAbsent(pidValue, k -> new HashSet<>()); // creates an entry in processtoPageFrameMap if one doesn't exist
        // this is only needed if a process tries to allocate more memory after it's creation, which doesn't happen yet in the system's current state

        for (int page = startPage; page <= endPage; page++) { // for each page to be allocated
            pages.add(page); // add to the list of pages owned by the process in processtoPageFrameMap
            frametoProcessMap.put(page, pidValue); // add to the list of pages and who owns them in pageFrametoProcessMap
        }

        return true;
    }

    public void freeProcessFrame(PidAllocator.Pid pid) {
        long pidValue = pid.getId();
        Set<Integer> pages = processtoFrameMap.remove(pidValue); // remove the entry in processtoPageFrameMap and store the list that was removed
        if (pages != null) {
            for (int page : pages) {
                frametoProcessMap.remove(page); // remove the pages/frames owned by the process in pageFrametoProcessMap
            }
        }
    }

    public void freeFrame(Integer pageFrame) {
        Long pid = frametoProcessMap.remove(pageFrame);
        if (pid != null) {
            Set<Integer> pages = processtoFrameMap.get(pid);
            if (pages != null) {
                pages.remove(pageFrame);
                if (pages.isEmpty()) {
                    processtoFrameMap.remove(pid);
                }
            }
        }
    }

    public boolean isFrameAllocated(int page) {
        return frametoProcessMap.containsKey(page);
    }

    public Long getFrameOwner(int page) {
        return frametoProcessMap.getOrDefault(page, null);
    }

    public Set<Integer> getProcessFrame(PidAllocator.Pid pid) {
        return processtoFrameMap.getOrDefault(pid.getId(), null);
    }

    public Map<Integer, Long> getAllocatedFrames() {
        return new HashMap<>(frametoProcessMap);
    }
    public boolean hasFreeFrames() {
        return (totalPages - frametoProcessMap.size()) > 0;
    }

    public int getTotalAllocatedFrames() {
        return frametoProcessMap.size();
    }
    public int getTotalProcessFrames(PidAllocator.Pid pid) {
        Set<Integer> frames = processtoFrameMap.get(pid.getId());
        return frames != null ? frames.size() : 0;
    }

    public OptionalInt findFirstContiguousFreeRange(int rangeSize) { // finds the first contiguous range of size "rangeSize" and returns the index where the range starts
        if (rangeSize <= 0 || rangeSize > totalPages) { // easy validations
            return OptionalInt.empty();
        }

        int consecutiveFree = 0;
        int startPage = -1;

        for (int page = 0; page < totalPages; page++) {
            if (!frametoProcessMap.containsKey(page)) {
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
        return (pid.getId() == frametoProcessMap.getOrDefault(pageNumber, null));
    }

}
